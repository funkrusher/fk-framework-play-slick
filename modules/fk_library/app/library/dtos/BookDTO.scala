package library.dtos

import core.dto.DTOImplicits
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import core.tables.Tables._
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel(value = "Book DTO", description = "A Book")
case class BookDTO(
    @ApiModelProperty(value = "The book's id", example = "17", dataType = "Long", required = false)
    id: Option[Long],
    @ApiModelProperty(value = "The author's id", example = "17", dataType = "Long", required = true)
    author_id: Long,
    @ApiModelProperty(value = "The book's title", example = "Lord of The Rings", dataType = "String", required = true)
    title: String,
    @ApiModelProperty(
      value = "The book's year of publishing",
      example = "1980",
      dataType = "String",
      required = true,
    )
    published_in: Int,
    @ApiModelProperty(
      value = "The book's language id",
      example = "1",
      dataType = "Int",
      required = true,
    )
    language_id: Int,
    @ApiModelProperty(value = "The book's bookstores", required = false)
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
