package library.dtos

import core.dto.DTOImplicits
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import core.tables.Tables._

case class BookStoreDTO(
    name: String
)

object BookStoreDTO extends DTOImplicits {
  implicit val fmt: OFormat[BookStoreDTO] = Json.format[BookStoreDTO]

  def fromRow(row: BookStoreRow): BookStoreDTO = {
    BookStoreDTO(
      name = row.name
    )
  }
}
