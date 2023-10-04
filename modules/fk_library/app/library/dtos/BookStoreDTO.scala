package library.dtos

import play.api.libs.json.Json
import play.api.libs.json.OFormat
import core.tables.Tables._
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel(value = "BookStore DTO", description = "A Book Store")
case class BookStoreDTO(
    @ApiModelProperty(value = "The bookstore's name", example = "The Bookship", dataType = "String", required = true)
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
