package library.dtos

import play.api.libs.json.Json
import play.api.libs.json.OFormat
import core.tables.Tables._

case class BookToBookStoreDTO(
    name: String,
    book_id: Int,
    stock: Option[Int],
) {
  def toRow(): BookToBookStoreRow = {
    BookToBookStoreRow(
      name = this.name,
      book_id = this.book_id,
      stock = this.stock,
    )
  }
}

object BookToBookStoreDTO {
  implicit val fmt: OFormat[BookToBookStoreDTO] = Json.format[BookToBookStoreDTO]
}
