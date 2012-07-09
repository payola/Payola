package cz.payola.data.squeryl

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import cz.payola.data._
import cz.payola.data.squeryl.entities._
import scala.Some
import cz.payola.data.PaginationInfo
import org.squeryl.dsl.ast.LogicalBoolean

trait TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    /**
      * A repository that fetches the entities from a Squeryl table.
      * @param table The corresponding table with the entities.
      * @param entityConverter A converter that converts to instances that can be stored into the table.
      * @tparam A Type of the entities in the repository.
      * @tparam B Type of select query result.
      */
    abstract class TableRepository[A <: PersistableEntity, B](val table: Table[A],
        val entityConverter: EntityConverter[A])
        extends Repository[A]
    {
        def getById(id: String): Option[A] = {
            select(id = Some(id)).headOption
        }

        def getAll(pagination: Option[PaginationInfo] = None): Seq[A] = {
            // TODO pagination
            select(id = None)
        }

        def removeById(id: String): Boolean = DataException.wrap {
            transaction {
                table.deleteWhere(e => id === e.id) == 1
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

        protected def select(query: Query[B]): Seq[A] = DataException.wrap {
            val results = transaction {
                query.toList
            }
            processSelectResults(results)
        }

        protected def select(id: Option[String]): Seq[A] = {
            select(getSelectQuery(id))
        }

        protected def getSelectQuery(id: Option[String]): Query[B]

        protected def processSelectResults(results: Seq[B]): Seq[A]

        protected def condition[C](option: Option[C], condition: C => LogicalBoolean): LogicalBoolean = {
            option.map(condition).getOrElse(1 === 1)
        }

        /**
          * Evaluates the specified query and returns only the first object in the result.
          *
          * @param query The query to evaluate.
          * @return The first object in the result.
          */
        final def evaluateSingleResultQuery[C](query: Query[C]): Option[C] = DataException.wrap {
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
        final def evaluateCollectionResultQuery[C](query: Query[C],
            pagination: Option[PaginationInfo] = None) = DataException.wrap {

            // Get all entities or paginate.
            val paginatedQuery = pagination.map(p => query.page(p.skip, p.limit)).getOrElse(query)

            transaction {
                paginatedQuery.toList
            }
        }
    }

    class LazyTableRepository[A <: PersistableEntity](table: Table[A], entityConverter: EntityConverter[A])
        extends TableRepository[A, A](table, entityConverter)
    {
        protected def getSelectQuery(id: Option[String]): Query[A] = {
            id.map(entityId => table.where(e => e.id === entityId)).getOrElse(table)
        }

        protected def processSelectResults(results: Seq[A]): Seq[A] = {
            results
        }
    }
}
