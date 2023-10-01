package codegen

import play.api.db.evolutions.{Evolution, EvolutionsReader, Script}

import scala.util.Try
import java.io.File
import java.nio.file.{Files, Paths}
import scala.io.Source
import scala.jdk.CollectionConverters.IteratorHasAsScala

class FilesystemEvolutionsReader(defaultDirectoryPath: String) extends EvolutionsReader {

  val absolutePath = new File(defaultDirectoryPath).getAbsolutePath
  val directory = new File(absolutePath)

  val groupedEvolutions: Map[String, Seq[Evolution]] = {
    if (directory.exists() && directory.isDirectory) {
      // List all files in the directory
      val fileList = Files.walk(Paths.get(defaultDirectoryPath)).iterator().asScala
        .filter(_.toFile.isFile)
        .map(_.toFile)
        .toList
      val files = fileList

      // Group files by the name (1.sql, 2.sql, etc.)
      val groupedFiles = files.groupBy(file => Try(file.getName.stripSuffix(".sql").toInt).getOrElse(0))

      // Sort the grouped files by name and extract the content of each file
      val sortedFiles = groupedFiles.toSeq.sortBy(_._1).flatMap { case (_, fileList) =>
        fileList.map { file =>
          val revision = file.getName.stripSuffix(".sql").toInt
          val (sqlUp, sqlDown) = parseSqlFile(file)
          Evolution(revision, sqlUp, sqlDown)
        }
      }

      // Group the sorted files by database name (empty string for default)
      Map[String, Seq[Evolution]]("default" -> sortedFiles.toSeq)
    } else {
      Map.empty
    }
  }

  private def parseSqlFile(file: File): (String, String) = {
    val fileContent = Source.fromFile(file).mkString
    val (sqlUp, sqlDown) = splitSqlContent(fileContent)
    (sqlUp, sqlDown)
  }

  private def splitSqlContent(content: String): (String, String) = {
    val downsIndex = content.indexOf("-- !Downs")
    if (downsIndex != -1) {
      val upsContent = content.substring(0, downsIndex).replaceAll("-- !Ups", "").trim
      val downsContent = content.substring(downsIndex + 9).trim
      (upsContent, downsContent)
    } else {
      // If no "-- !Downs" marker is found, consider the entire content as "sql_up"
      (content.replaceAll("-- !Ups", "").trim, "")
    }
  }

  override def evolutions(dbName: String): Seq[Evolution] = {
    groupedEvolutions.getOrElse(dbName, Seq.empty)
  }
}