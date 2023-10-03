package library.dtos

import play.api.libs.json.Json
import play.api.libs.json.OFormat
import core.tables.Tables._
case class BookStoreDTO(
    name: String
)

object BookStoreDTO {
  implicit val fmt: OFormat[BookStoreDTO] = Json.format[BookStoreDTO]

  def fromRow(row: BookStoreRow): BookStoreDTO = {
    BookStoreDTO(
      name = row.name
    )
  }
}
