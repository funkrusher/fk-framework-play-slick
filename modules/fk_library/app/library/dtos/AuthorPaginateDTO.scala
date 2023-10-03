package library.dtos

import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class AuthorPaginateDTO(authors: Seq[AuthorDTO], count: Int)

object AuthorPaginateDTO {
  implicit val fmt: OFormat[AuthorPaginateDTO] = Json.format[AuthorPaginateDTO]
}
