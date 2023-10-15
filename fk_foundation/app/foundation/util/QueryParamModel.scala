package foundation.util

import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class QueryParamModel(
    drop: Option[Int],
    take: Option[Int],
    sorter: Option[QueryParamSorterModel],
    filters: Option[Seq[QueryParamFilterModel]],
)

object QueryParamModel {
  implicit val fmt: OFormat[QueryParamModel] = Json.format[QueryParamModel]
}

case class QueryParamSorterModel(
    tableName: String,
    sortOrder: String,
    sortName: String,
)

object QueryParamSorterModel {
  implicit val fmt: OFormat[QueryParamSorterModel] = Json.format[QueryParamSorterModel]
}

case class QueryParamFilterModel(
    tableName: String,
    filterName: String,
    filterValue: String,
    filterComparator: String,
)

object QueryParamFilterModel {
  implicit val fmt: OFormat[QueryParamFilterModel] = Json.format[QueryParamFilterModel]
}
