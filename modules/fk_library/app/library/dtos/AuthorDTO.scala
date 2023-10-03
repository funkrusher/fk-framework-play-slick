package library.dtos

import play.api.libs.json.Json
import play.api.libs.json.OFormat
import core.tables.Tables._
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import java.sql.Date

@ApiModel(value = "Author DTO", description = "An Author of Books")
case class AuthorDTO(
    @ApiModelProperty(value = "The author's id", example = "17", dataType = "Int", required = false)
    id: Option[Int],
    @ApiModelProperty(value = "The author's first name", example = "Mike", dataType = "String", required = false)
    first_name: Option[String],
    @ApiModelProperty(value = "The author's first name", example = "Mike", dataType = "String", required = true)
    last_name: String,
    @ApiModelProperty(
      value = "The author's date of birth",
      example = "1980-01-01",
      dataType = "String",
      required = false,
    )
    date_of_birth: Option[Date],
    @ApiModelProperty(
      value = "The author's year of birth",
      example = "1980",
      dataType = "String",
      required = false,
    )
    year_of_birth: Option[Int],
    @ApiModelProperty(
      value = "The author's distinguished flag",
      example = "1",
      dataType = "Int",
      required = false,
    )
    distinguished: Option[Int],
    books: Option[Seq[BookDTO]],
) {
  def toRow(): AuthorRow = {
    AuthorRow(
      id = this.id,
      first_name = this.first_name,
      last_name = this.last_name,
      date_of_birth = this.date_of_birth,
      year_of_birth = this.year_of_birth,
      distinguished = this.distinguished,
    )
  }
}

object AuthorDTO {
  implicit val fmt: OFormat[AuthorDTO] = Json.format[AuthorDTO]

  def fromRow(row: AuthorRow): AuthorDTO = {
    AuthorDTO(
      id = row.id,
      first_name = row.first_name,
      last_name = row.last_name,
      date_of_birth = row.date_of_birth,
      year_of_birth = row.year_of_birth,
      distinguished = row.distinguished,
      books = None,
    )
  }
}
