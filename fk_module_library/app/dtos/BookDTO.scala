package dtos

import play.api.libs.json.{Json, OFormat}
import tables.Tables._

case class BookDTO(
                    id: Int,
                    author_id: Int,
                    title: String,
                    published_in: Int,
                    language_id: Int,
                    bookStores: Option[Seq[BookStoreDTO]]
                    )

object BookDTO {
  implicit val fmt: OFormat[BookDTO] = Json.format[BookDTO]

  def fromRow(row: BookRow): BookDTO = {
    BookDTO(
      id = row.id,
      author_id = row.author_id,
      title = row.title,
      published_in = row.published_in,
      language_id = row.language_id,
      bookStores = None
    )
  }
}