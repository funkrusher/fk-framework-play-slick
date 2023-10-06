package core.dao

import slick.lifted.AbstractTable

abstract class MultiKeyDAO[V <: AbstractTable[R], R <: V#TableElementType, I <: Any] extends DAO[V, R, I] {

  import profile.api._

  override def config: MultiKeyQueryConfig

  abstract class MultiKeyQueryConfig(q: TableQuery[V]) extends QueryConfig(q) {}
}
