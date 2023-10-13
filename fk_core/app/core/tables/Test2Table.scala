package core.tables
// AUTO-GENERATED Slick data model for table Test2
trait Test2Table {

  self:TablesRoot  =>

  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}
  /** Entity class storing rows of table Test2
   *  @param description Database column description SqlType(VARCHAR), Length(50,true), Default(Some(NULL))
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey */
  case class Test2Row(description: Option[String], id: Option[Int])
  /** GetResult implicit for fetching Test2Row objects using plain SQL queries */
  implicit def GetResultTest2Row(implicit e0: GR[Option[String]], e1: GR[Option[Int]]): GR[Test2Row] = GR{
    prs => import prs._
    val r = (<<?[Int], <<?[String])
    import r._
    Test2Row.tupled((_2, _1)) // putting AutoInc last
  }
  /** Table description of table test2. Objects of this class serve as prototypes for rows in queries. */
  class Test2(_tableTag: Tag) extends profile.api.Table[Test2Row](_tableTag, Some("mylibrary"), "test2") {
    def * = (description, Rep.Some(id)).<>(Test2Row.tupled, Test2Row.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((description, Rep.Some(id))).shaped.<>({r=>import r._; _2.map(_=> Test2Row.tupled((_1, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column description SqlType(VARCHAR), Length(50,true), Default(Some(NULL)) */
    val description: Rep[Option[String]] = column[Option[String]]("description", O.Length(50,varying=true))
    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
  }
  /** Collection-like TableQuery object for table Test2 */
  lazy val Test2 = new TableQuery(tag => new Test2(tag))
}
