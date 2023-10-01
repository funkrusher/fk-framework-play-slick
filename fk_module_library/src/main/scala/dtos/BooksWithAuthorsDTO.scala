package dtos

import play.api.libs.json.{Json, OFormat}

case class BooksWithAuthorsDTO(
                                  id: Int,
                                  authors: Seq[AuthorDTO]
                                )

object BooksWithAuthorsDTO {
    implicit val fmt: OFormat[BooksWithAuthorsDTO] = Json.format[BooksWithAuthorsDTO]
}

