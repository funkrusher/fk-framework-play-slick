package codegen

import com.typesafe.config.ConfigFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.utility.DockerImageName
import play.api.db.evolutions.ThisClassLoaderEvolutionsReader.evolutions
import play.api.db.evolutions.Evolutions
import play.api.db.evolutions.EvolutionsReader
import play.api.db.evolutions.SimpleEvolutionsReader
import slick.codegen.SourceCodeGenerator
import slick.jdbc.MySQLProfile
import slick.jdbc.meta.MTable
import play.api.db.Database
import play.api.db.Databases
import play.db.Database
import play.db.evolutions.EvolutionsReader

import java.io.File
import java.sql.DriverManager
import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.reflect.io.Directory

class SlickCodegen {

  def rebuild(clearOld: Boolean) = {
    println("SlickCodegen - start")

    // lets first define all fixed variables we need.
    val projectPath    = "./services/fk_backend"
    val evolutionsPath = projectPath + "/conf/evolutions/default"
    val databaseName   = "mylibrary"
    val username       = "codegen"
    val password       = "codegen"
    val slickDriver    = "slick.jdbc.MySQLProfile"
    val jdbcDriver     = "org.mariadb.jdbc.Driver"
    val outputDir      = "./fk_foundation/app" // be careful! define the exact path, as it will be deleted during reruns.
    val pkg            = "foundation.tables"   // be careful! define the exact package, as it will be deleted during reruns.

    // Define the MariaDB test container
    var mariaDBContainer: MariaDBContainer[_] =
      new MariaDBContainer(DockerImageName.parse("mariadb:10.4.17")).withDatabaseName(databaseName)
    mariaDBContainer = mariaDBContainer.withUsername(username).asInstanceOf[MariaDBContainer[_]]
    mariaDBContainer = mariaDBContainer.withPassword(password).asInstanceOf[MariaDBContainer[_]]

    // Start the MariaDB container
    mariaDBContainer.start()

    waitUntilContainerIsRunning(mariaDBContainer)

    // Apply Play evolutions to initialize the database
    val database = Databases(
      driver = jdbcDriver,
      url = mariaDBContainer.getJdbcUrl,
      config = Map("username" -> username, "password" -> password),
    )

    try {
      val db = slick.jdbc.MySQLProfile.api.Database
        .forURL(mariaDBContainer.getJdbcUrl, username, password, driver = jdbcDriver)

      // Create the 'mylibrary' database if it doesn't exist
      createDatabaseIfNotExists(mariaDBContainer.getJdbcUrl, username, password, databaseName)

      val fsEvolutionsReader = new FilesystemEvolutionsReader(evolutionsPath)

      try {
        Evolutions.applyEvolutions(database, fsEvolutionsReader)
      } catch {
        case e: Exception =>
          println("SlickCodegen - evolutions error!")
          e.printStackTrace()
          throw e
      }

      if (clearOld) {
        println("SlickCodegen - clear old files...")
        val folder2: String = outputDir + "/" + (pkg.replace(".", "/"))
        val deleteFile      = new File(folder2)
        if (deleteFile.exists()) {
          val dir     = new Directory(deleteFile)
          val deleted = dir.deleteRecursively()
          if (!deleted) {
            throw new Exception("could not delete old tables! unable to delete.")
          }
          println("SlickCodegen - clear old files done! " + deleted)
        } else {
          throw new Exception("could not delete old tables! not found.")
        }
      }

      val dbio =
        MySQLProfile.createModel(Some(MTable.getTables(Some(databaseName), None, None, Some(Seq("TABLE", "VIEW")))))
      val futureModel                         = db.run(dbio)
      val future: Future[SourceCodeGenerator] = futureModel.map(model => new SimpleCodeGenerator(model))
      try {
        println("SlickCodegen - creating files...")
        val codegen: SourceCodeGenerator = Await.result(future, Duration.create(5, TimeUnit.MINUTES))
        codegen.writeToMultipleFiles(
          profile = slickDriver,
          folder = outputDir,
          pkg = pkg,
          container = "Tables",
        )
        println("SlickCodegen - creating files done!")
      } catch {
        case e: Exception =>
          println("SlickCodegen - creating files error!")
          e.printStackTrace()
      }

    } finally {
      // Stop the MariaDB container when done
      mariaDBContainer.stop()
    }
  }

  def waitUntilContainerIsRunning(container: MariaDBContainer[_]): Unit = {
    while (!container.isRunning) {
      Thread.sleep(1000) // Wait for 1 second
    }
  }

  def createDatabaseIfNotExists(url: String, username: String, password: String, dbName: String): Unit = {
    // Create the database if it doesn't exist
    val connection  = DriverManager.getConnection(url, username, password)
    val statement   = connection.createStatement()
    val createDbSQL = s"CREATE DATABASE IF NOT EXISTS $dbName"
    statement.executeUpdate(createDbSQL)
    connection.close()
  }
}
