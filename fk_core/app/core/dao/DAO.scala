package core.dao

import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

abstract class DAO extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val logger: Logger = Logger(this.getClass())

  val NEVER_TRUE  = LiteralColumn(1) === LiteralColumn(0)
  val ALWAYS_TRUE = LiteralColumn(1) === LiteralColumn(1)
}
