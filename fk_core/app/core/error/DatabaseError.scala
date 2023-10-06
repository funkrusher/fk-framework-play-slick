package core.error

/**
 * DatabaseError
 *
 * Exceptions related to the database or infrastructure, like DatabaseError,
 * are typically caught and handled in lower layers, as these layers are closer to the source of the error.
 * Handling them allows you to log and potentially take corrective actions, such as retries.
 *
 * This error can occur only in the Manager-Layer as it's the only layer executing the database-transactions.
 *
 * @param message
 */
case class DatabaseError(message: String) extends ManagerError
