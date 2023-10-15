package foundation.dtos

import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class QueryParamDTO(
    drop: Option[Int],
    take: Option[Int],
    sorter: Option[QueryParamSorterDTO],
    filters: Option[Seq[QueryParamFilterDTO]],
)

object QueryParamDTO {
  implicit val fmt: OFormat[QueryParamDTO] = Json.format[QueryParamDTO]
}

case class QueryParamSorterDTO(
    tableName: String,
    sortOrder: String,
    sortName: String,
)

object QueryParamSorterDTO {
  implicit val fmt: OFormat[QueryParamSorterDTO] = Json.format[QueryParamSorterDTO]
}

case class QueryParamFilterDTO(
    tableName: String,
    filterName: String,
    filterValue: String,
    filterComparator: String,
)

object QueryParamFilterDTO {
  implicit val fmt: OFormat[QueryParamFilterDTO] = Json.format[QueryParamFilterDTO]
}
