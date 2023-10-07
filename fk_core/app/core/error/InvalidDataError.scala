package core.error

/**
 * InvalidDataError
 *
 * A specific mapping error that occurs when the data being processed is invalid
 * or does not conform to the expected format or constraints.
 * This error indicates that the data cannot be correctly transformed into the desired form.
 *
 * Usage: InvalidDataError is used when there are issues with the quality or validity of the data,
 * making it unsuitable for mapping.
 *
 * @param message
 */
case class InvalidDataError(message: String) extends MappingError
