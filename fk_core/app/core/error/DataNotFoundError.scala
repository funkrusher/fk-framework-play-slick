package core.error

/**
 * DataNotFoundError
 *
 * A specific mapping error that occurs when the required data cannot be found or retrieved.
 * This error typically indicates that the data necessary for the mapping operation is missing or unavailable.
 *
 * Usage: DataNotFoundError is used when an expected piece of data is not present, preventing successful mapping.
 *
 * @param message
 */
case class DataNotFoundError(message: String) extends MappingError
