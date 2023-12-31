package foundation.tables
// AUTO-GENERATED Slick data model for table Author
trait AuthorTable {

  self:TablesRoot  =>

  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}
  /** Entity class storing rows of table Author
   *  @param first_name Database column first_name SqlType(VARCHAR), Length(50,true), Default(Some(NULL))
   *  @param last_name Database column last_name SqlType(VARCHAR), Length(50,true)
   *  @param date_of_birth Database column date_of_birth SqlType(DATE), Default(None)
   *  @param year_of_birth Database column year_of_birth SqlType(INT), Default(None)
   *  @param created_at Database column created_at SqlType(TIMESTAMP)
   *  @param reviewed_at Database column reviewed_at SqlType(TIMESTAMP), Default(None)
   *  @param distinguished Database column distinguished SqlType(INT), Default(None)
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
  case class AuthorRow(first_name: Option[String], last_name: String, date_of_birth: Option[java.sql.Date], year_of_birth: Option[Int], created_at: java.sql.Timestamp, reviewed_at: Option[java.sql.Timestamp], distinguished: Option[Int], id: Option[Long])
  /** GetResult implicit for fetching AuthorRow objects using plain SQL queries */
  implicit def GetResultAuthorRow(implicit e0: GR[Option[String]], e1: GR[String], e2: GR[Option[java.sql.Date]], e3: GR[Option[Int]], e4: GR[java.sql.Timestamp], e5: GR[Option[java.sql.Timestamp]], e6: GR[Option[Long]]): GR[AuthorRow] = GR{
    prs => import prs._
    val r = (<<?[Long], <<?[String], <<[String], <<?[java.sql.Date], <<?[Int], <<[java.sql.Timestamp], <<?[java.sql.Timestamp], <<?[Int])
    import r._
    AuthorRow.tupled((_2, _3, _4, _5, _6, _7, _8, _1)) // putting AutoInc last
  }
  /** Table description of table author. Objects of this class serve as prototypes for rows in queries. */
  class Author(_tableTag: Tag) extends profile.api.Table[AuthorRow](_tableTag, Some("mylibrary"), "author") {
    def * = (first_name, last_name, date_of_birth, year_of_birth, created_at, reviewed_at, distinguished, Rep.Some(id)).<>(AuthorRow.tupled, AuthorRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((first_name, Rep.Some(last_name), date_of_birth, year_of_birth, Rep.Some(created_at), reviewed_at, distinguished, Rep.Some(id))).shaped.<>({r=>import r._; _2.map(_=> AuthorRow.tupled((_1, _2.get, _3, _4, _5.get, _6, _7, _8)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column first_name SqlType(VARCHAR), Length(50,true), Default(Some(NULL)) */
    val first_name: Rep[Option[String]] = column[Option[String]]("first_name", O.Length(50,varying=true))
    /** Database column last_name SqlType(VARCHAR), Length(50,true) */
    val last_name: Rep[String] = column[String]("last_name", O.Length(50,varying=true))
    /** Database column date_of_birth SqlType(DATE), Default(None) */
    val date_of_birth: Rep[Option[java.sql.Date]] = column[Option[java.sql.Date]]("date_of_birth")
    /** Database column year_of_birth SqlType(INT), Default(None) */
    val year_of_birth: Rep[Option[Int]] = column[Option[Int]]("year_of_birth")
    /** Database column created_at SqlType(TIMESTAMP) */
    val created_at: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("created_at")
    /** Database column reviewed_at SqlType(TIMESTAMP), Default(None) */
    val reviewed_at: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("reviewed_at")
    /** Database column distinguished SqlType(INT), Default(None) */
    val distinguished: Rep[Option[Int]] = column[Option[Int]]("distinguished")
    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
  }
  /** Collection-like TableQuery object for table Author */
  lazy val Author = new TableQuery(tag => new Author(tag))
}
