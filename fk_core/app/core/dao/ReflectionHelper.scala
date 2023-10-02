package core.dao

import play.api.Logger
import slick.lifted.AbstractTable

import java.lang.reflect.Method
import java.lang.reflect.Type

object ReflectionHelper {
  val logger: Logger = Logger(this.getClass())

  /**
   * Prepare the reflector that can be used to resolve a value in the given table for the given columnName
   *
   * @param table      table
   * @param columnName columnName
   * @return reflector or exception
   */
  def prepareReflector(table: AbstractTable[_], columnName: String): Method = {
    try {
      table.getClass.getMethod(columnName)
    } catch {
      case ex: Exception =>
        val message = s"""Column $columnName in table ${table.tableName} not found!"""
        logger.error(message, ex)
        throw new RuntimeException(message)
    }
  }

}
