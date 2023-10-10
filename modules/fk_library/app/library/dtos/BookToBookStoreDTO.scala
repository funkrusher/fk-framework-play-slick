package library.dtos

import core.dto.DTOImplicits
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import core.tables.Tables._
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel(value = "BookToBookStore DTO", description = "A relationship between books and bookstores")
case class BookToBookStoreDTO(
    @ApiModelProperty(value = "The bookstore's name", example = "The Bookship", dataType = "String", required = true)
    name: String,
    @ApiModelProperty(value = "The book's id", example = "17", dataType = "Long", required = true)
    book_id: Long,
    @ApiModelProperty(
      value = "The stock count of this book in this bookstore",
      example = "250",
      dataType = "Int",
      required = false,
    )
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

object BookToBookStoreDTO extends DTOImplicits {
  implicit val fmt: OFormat[BookToBookStoreDTO] = Json.format[BookToBookStoreDTO]
}
