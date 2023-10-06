package core.error.mapping

import core.error.ManagerError

/**
 * MappingError
 *
 * An abstract error type that represents issues related to the mapping of data from one form to another.
 * Mapping errors can occur when transforming data into a different structure or format due to various issues.
 *
 * Usage: MappingError serves as a generic error type for all mapping-related errors
 * and can be extended with specific error classes to cover different mapping-related scenarios.
 *
 * @param message
 */
trait MappingError extends ManagerError {
  def message: String
}

object MappingError {
  def apply(message: String): MappingError = GenericMappingError(message)
}
