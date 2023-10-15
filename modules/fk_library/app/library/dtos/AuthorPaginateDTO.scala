package library.dtos

import foundation.dto.DTOImplicits
import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class AuthorPaginateDTO(
    authors: Seq[AuthorDTO],
    count: Int,
)

object AuthorPaginateDTO extends DTOImplicits {
  implicit val fmt: OFormat[AuthorPaginateDTO] = Json.format[AuthorPaginateDTO]
}
