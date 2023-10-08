package core.dao

import com.google.inject.ImplementedBy
import slick.lifted.AbstractTable

trait MultiKeyDAO[V <: AbstractTable[R], R <: V#TableElementType, I <: Any] extends DAO[V, R, I] {}

abstract class MultiKeyDAOImpl[V <: AbstractTable[R], R <: V#TableElementType, I <: Any]
    extends DAOImpl[V, R, I]
    with MultiKeyDAO[V, R, I] {

  import profile.api._

  override def config: MultiKeyQueryConfig

  abstract class MultiKeyQueryConfig(q: TableQuery[V]) extends QueryConfig(q) {}
}
