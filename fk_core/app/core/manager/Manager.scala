package manager

import manager.errors.ManagerError
import persistence.errors.DbError

abstract class Manager {

  def toManagerError(dbError: DbError) = ManagerError(dbError.message)

}
