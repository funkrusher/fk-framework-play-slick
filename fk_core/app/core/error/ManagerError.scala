package core.error

trait ManagerError extends Throwable {
  def message: String
}
