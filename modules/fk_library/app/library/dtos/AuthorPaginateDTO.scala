package library.dtos

import core.dto.DTOImplicits
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import play.api.libs.json.Json
import play.api.libs.json.OFormat

@ApiModel(value = "AuthorPaginate DTO", description = "A paginated result of authors")
case class AuthorPaginateDTO(
    @ApiModelProperty(value = "The paginated authors", required = true)
    authors: Seq[AuthorDTO],
    @ApiModelProperty(value = "The paginated authors count", required = true)
    count: Int,
)

object AuthorPaginateDTO extends DTOImplicits {
  implicit val fmt: OFormat[AuthorPaginateDTO] = Json.format[AuthorPaginateDTO]
}
