package core.util

import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class QueryParamModel(
    drop: Option[Int],
    take: Option[Int],
    sorter: Option[QueryParamSorterModel],
    filters: Option[Seq[QueryParamFilterModel]],
) {

//    def resolveConditionByName(tableName: String, filterName: String): Option[Rep[Option[Boolean]]] = {
//      val maybeFilter = findFilterByName(tableName, filterName)
//      maybeFilter match {
//        case Some(filter) => filter.
//        case _ => None
//      }
//    }

  def findFilterByName(tableName: String, filterName: String): Option[QueryParamFilterModel] =
    filters match {
      case Some(list) => list.find(x => x.tableName == tableName && x.filterName == filterName)
      case _          => None
    }

}

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
