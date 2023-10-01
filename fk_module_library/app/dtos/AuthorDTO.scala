package dtos

import play.api.libs.json.{Json, OFormat}
import tables.Tables._

import java.sql.Date

case class AuthorDTO(
                      id: Int,
                      first_name: Option[String],
                      last_name: String,
                      date_of_birth: Option[Date],
                      year_of_birth: Option[Int],
                      distinguished: Option[Int],
                      books: Option[Seq[BookDTO]]
                    ) {
  def toRow(): AuthorRow = {
    AuthorRow(
      id = this.id,
      first_name = this.first_name,
      last_name = this.last_name,
      date_of_birth = this.date_of_birth,
      year_of_birth = this.year_of_birth,
      distinguished = this.distinguished
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
      books = None
    )
  }
}