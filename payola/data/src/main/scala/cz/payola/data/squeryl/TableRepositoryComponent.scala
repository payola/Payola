package cz.payola.data.squeryl

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import cz.payola.data._
import cz.payola.data.squeryl.entities._
import scala.Some
import cz.payola.data.PaginationInfo

trait TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    class TableRepository[A <: PersistableEntity](val table: Table[A], val entityConverter: EntityConverter[A])
        extends Repository[A]
    {
        def getById(id: String): Option[A] = DataException.wrap {
            evaluateSingleResultQuery(table.where(e => e.id === id))
        }

        def removeById(id: String): Boolean = DataException.wrap {
            transaction {
                table.deleteWhere(e => id === e.id) == 1
            }
        }

        def getAll(pagination: Option[PaginationInfo] = None): Seq[A] = {
            evaluateCollectionResultQuery(table, pagination)
        }

        /**
          * Evaluates the specified query and returns only the first object in the result.
          *
          * @param query The query to evaluate.
          * @return The first object in the result.
          */
        final def evaluateSingleResultQuery[A](query: Query[A]): Option[A] = DataException.wrap {
            transaction {
                if (query.size > 0) Some(query.single) else None
            }
        }

        /**
          * Evaluates the specified query that returns collection of objects as a result.
          * @param query The query to evaluate.
          * @param pagination Optionally specified pagination of the query.
          * @return Returns the result of the query.
          */
        final def evaluateCollectionResultQuery[A](query: Query[A],
            pagination: Option[PaginationInfo] = None) = DataException.wrap {

            // Get all entities or paginate.
            val paginatedQuery = pagination.map(p => query.page(p.skip, p.limit)).getOrElse(query)

            transaction {
                paginatedQuery.toList
            }
        }

        def persist(entity: AnyRef): A = DataException.wrap {
            val convertedEntity = entityConverter(entity)
            transaction {
                if (getById(convertedEntity.id).isDefined) {
                    table.update(convertedEntity)
                } else {
                    table.insert(convertedEntity)
                }
            }
            convertedEntity
        }
    }
}
