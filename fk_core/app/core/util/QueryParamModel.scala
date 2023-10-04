package core.util

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import play.api.libs.json.Json
import play.api.libs.json.OFormat

@ApiModel(value = "QueryParamModel", description = "Remote-Pagination Query-Parameters")
case class QueryParamModel(
    @ApiModelProperty(value = "The remote pagination position", example = "0", dataType = "Int", required = false)
    drop: Option[Int],
    @ApiModelProperty(value = "The remote pagination window-size", example = "10", dataType = "Int", required = false)
    take: Option[Int],
    @ApiModelProperty(value = "The remote pagination's sorter setting", required = false)
    sorter: Option[QueryParamSorterModel],
    @ApiModelProperty(value = "The remote pagination's filters setting", required = false)
    filters: Option[Seq[QueryParamFilterModel]],
)

object QueryParamModel {
  implicit val fmt: OFormat[QueryParamModel] = Json.format[QueryParamModel]
}

@ApiModel(value = "QueryParamSorterModel", description = "Remote-Pagination Sorter-Setting")
case class QueryParamSorterModel(
    @ApiModelProperty(value = "Tablename sorted on", example = "MyTable", dataType = "String", required = true)
    tableName: String,
    @ApiModelProperty(value = "Sorting Order (asc/desc)", example = "desc", dataType = "String", required = true)
    sortOrder: String,
    @ApiModelProperty(value = "Column-Name sorted on", example = "MyColumn", dataType = "String", required = true)
    sortName: String,
)

object QueryParamSorterModel {
  implicit val fmt: OFormat[QueryParamSorterModel] = Json.format[QueryParamSorterModel]
}

@ApiModel(value = "QueryParamFilterModel", description = "Remote-Pagination Filter-Setting")
case class QueryParamFilterModel(
    @ApiModelProperty(value = "Tablename filtered on", example = "MyTable", dataType = "String", required = true)
    tableName: String,
    @ApiModelProperty(value = "Column-Name filtered on", example = "MyColumn", dataType = "String", required = true)
    filterName: String,
    @ApiModelProperty(value = "Filter-Value", example = "Diaphragma", dataType = "String", required = true)
    filterValue: String,
    @ApiModelProperty(
      value = "Filter-Comparator (like,equal,...)",
      example = "like",
      dataType = "String",
      required = true,
    )
    filterComparator: String,
)

object QueryParamFilterModel {
  implicit val fmt: OFormat[QueryParamFilterModel] = Json.format[QueryParamFilterModel]
}
