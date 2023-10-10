package library.dtos

import core.dto.DTOImplicits
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import core.tables.Tables._
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDate

@ApiModel(value = "Author DTO", description = "An Author of Books")
case class AuthorDTO(
    @ApiModelProperty(value = "The author's id", example = "17", dataType = "Long", required = false)
    id: Option[Long],
    @ApiModelProperty(value = "The author's first name", example = "Mike", dataType = "String", required = false)
    first_name: Option[String],
    @ApiModelProperty(value = "The author's last name", example = "Tyson", dataType = "String", required = true)
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
      value = "The author's createdAt (milliseconds since 1970)",
      example = "12347564643",
      dataType = "Long",
      required = true,
    )
    created_at: Timestamp,
    @ApiModelProperty(
      value = "The author's reviewedAt (milliseconds since 1970)",
      example = "12347564643",
      dataType = "Long",
      required = false,
    )
    reviewed_at: Option[Timestamp],
    @ApiModelProperty(
      value = "The author's distinguished flag",
      example = "1",
      dataType = "Int",
      required = false,
    )
    distinguished: Option[Int],
    @ApiModelProperty(value = "The author's books", required = false)
    books: Option[Seq[BookDTO]],
) {
  def toRow(): AuthorRow = {
    AuthorRow(
      id = this.id,
      first_name = this.first_name,
      last_name = this.last_name,
      date_of_birth = this.date_of_birth,
      year_of_birth = this.year_of_birth,
      created_at = this.created_at,
      reviewed_at = this.reviewed_at,
      distinguished = this.distinguished,
    )
  }
}

object AuthorDTO extends DTOImplicits {
  implicit val fmt: OFormat[AuthorDTO] = Json.format[AuthorDTO]

  def fromRow(row: AuthorRow): AuthorDTO = {
    AuthorDTO(
      id = row.id,
      first_name = row.first_name,
      last_name = row.last_name,
      date_of_birth = row.date_of_birth,
      year_of_birth = row.year_of_birth,
      created_at = row.created_at,
      reviewed_at = row.reviewed_at,
      distinguished = row.distinguished,
      books = None,
    )
  }
}
