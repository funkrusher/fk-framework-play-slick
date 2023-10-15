package library.dtos

import foundation.dto.DTOImplicits
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import foundation.tables.Tables._

import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDate

case class AuthorDTO(
    id: Option[Long],
    first_name: Option[String],
    last_name: String,
    date_of_birth: Option[Date],
    year_of_birth: Option[Int],
    created_at: Timestamp,
    reviewed_at: Option[Timestamp],
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
