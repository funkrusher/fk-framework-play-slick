package dtos

import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.json.{Json, OFormat}

case class BookToBookStoreDTO(
                                 name: String,
                                 book_id: Int,
                                 stock: Int
                               )

object BookToBookStoreDTO {
    implicit val fmt: OFormat[BookToBookStoreDTO] = Json.format[BookToBookStoreDTO]
}
