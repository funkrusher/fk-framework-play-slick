package core.manager

import core.manager.errors.ManagerError
import core.persistence.errors.DbError

abstract class Manager {

  def toManagerError(dbError: DbError) = ManagerError(dbError.message)

}
