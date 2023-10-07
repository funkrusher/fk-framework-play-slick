package core.error

/**
 * ValidationError
 *
 * This exception typically represents errors related to input validation or domain-specific validation rules.
 * It is often thrown in the business logic layer (e.g., service or manager layer) when validating data
 * before performing an operation. These errors are usually not related to HTTP-specific concerns
 * and are better handled in lower layers.
 *
 * For our application those are: DAO, Repository, Manager
 *
 * @param message
 */
case class ValidationError(message: String) extends MappingError
