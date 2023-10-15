package library.dtos

import foundation.dto.DTOImplicits
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import foundation.tables.Tables._

case class BookDTO(
    id: Option[Long],
    author_id: Long,
    title: String,
    published_in: Int,
    language_id: Int,
    bookStores: Option[Seq[BookStoreDTO]],
)

object BookDTO extends DTOImplicits {
  implicit val fmt: OFormat[BookDTO] = Json.format[BookDTO]

  def fromRow(row: BookRow): BookDTO = {
    BookDTO(
      id = row.id,
      author_id = row.author_id,
      title = row.title,
      published_in = row.published_in,
      language_id = row.language_id,
      bookStores = None,
    )
  }
}
