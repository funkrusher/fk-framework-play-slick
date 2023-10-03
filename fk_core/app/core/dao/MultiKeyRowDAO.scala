package core.dao

import slick.lifted.AbstractTable

abstract class MultiKeyRowDAO[V <: AbstractTable[R], R <: V#TableElementType, I <: Any] extends BaseRowDAO[V, R, I] {

  import profile.api._

  override def config: MultiKeyQueryConfig

  abstract class MultiKeyQueryConfig(q: TableQuery[V]) extends QueryConfig(q) {}
}
