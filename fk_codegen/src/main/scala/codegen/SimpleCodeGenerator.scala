package codegen

import slick.codegen.SourceCodeGenerator
import slick.relational.RelationalProfile.ColumnOption.Default

class SimpleCodeGenerator(model: slick.model.Model) extends SourceCodeGenerator(model) {

  override def tableName =
    dbTableName => dbTableName.toLowerCase.toCamelCase

  override def Table = {
    new Table(_) {

      override val autoIncLastAsOption = true

      // override contained column generator
      override def Column =
        new Column(_) {

          override def rawName = this.model.name

          // we currently don't need default-values in the columns of the table-definition, let's disable those,
          // until following is fixed: https://github.com/slick/slick/issues/2827
          override def default: Option[String] = None

          // we currently don't need default-values in the columns of the table-definition, let's disable those,
          // until following is fixed: https://github.com/slick/slick/issues/2827
          override def columnOptionCode = { option =>
            {
              option match {
                case Default(_) => None                           // we currently don't need defaults.
                case _          => super.columnOptionCode(option) // we still need other options!
              }
            }
          }
        }
    }
  }
}
