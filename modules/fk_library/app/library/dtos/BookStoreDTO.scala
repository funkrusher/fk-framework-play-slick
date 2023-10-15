package library.dtos

import foundation.dto.DTOImplicits
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import foundation.tables.Tables._

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
