package foundation.error

/**
 * ObjectNotFoundError
 *
 * This exception is used when a requested resource or object cannot be found.
 * It's thrown when attempting to retrieve or manipulate data that doesn't exist in the system.
 * Object not found errors are typically more related to the core functionality of your application
 * and are appropriately handled in lower layers, such as the data access layer or service layer.
 *
 * For our application those are: DAO, Repository, Manager
 *
 * @param message
 */
case class ObjectNotFoundError(message: String) extends MappingError
