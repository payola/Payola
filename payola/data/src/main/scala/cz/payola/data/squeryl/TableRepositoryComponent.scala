package cz.payola.data.squeryl

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.ast.LogicalBoolean
import cz.payola.data._
import cz.payola.data.squeryl.entities._
import cz.payola.domain.entities._
import cz.payola.data.PaginationInfo

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
        def getByIds(ids: Seq[String]): Seq[A] = {
            selectWhere(entity => entity.id in ids)
        }

        def getAll(pagination: Option[PaginationInfo] = None): Seq[A] = {
            // TODO pagination
            selectWhere(_ => 1 === 1)
        }

        def removeById(id: String): Boolean = wrapInTransaction {
            table.deleteWhere(e => id === e.id) == 1
        }

        def persist(entity: AnyRef): A = wrapInTransaction {
            val convertedEntity = entityConverter(entity)
            if (getById(convertedEntity.id).isDefined) {
                table.update(convertedEntity)
            } else {
                table.insert(convertedEntity)
            }
            convertedEntity
        }

        def getCount: Long = wrapInTransaction {
            from(table)(e => compute(count))
        }

        /**
          * Executes the specified query and returns its results.
          * @param query The query to execute.
          * @return Results of the query.
          */
        protected def select(query: Query[B]): Seq[A] = {
            processSelectResults(query.toList)
        }

        /**
          * Selects all entities that pass the specified entity filter.
          * @param entityFilter A filter that excludes enitites from the result.
          * @return The selected entities.
          */
        private[squeryl] def selectWhere(entityFilter: A => LogicalBoolean): Seq[A] = wrapInTransaction {
            select(getSelectQuery(entityFilter))
        }

        /**
          * Selects the first entity that passes the specified entity filter.
          * @param entityFilter A filter that excludes enitites from the result.
          * @return The selected entity.
          */
        private[squeryl] def selectOneWhere(entityFilter: A => LogicalBoolean): Option[A] = {
            selectWhere(entityFilter).headOption
        }

        /**
          * Returns a query that should be used when selecting entities from the database.
          * @param entityFilter Filters entities that should be selected.
          * @return The select query.
          */
        protected def getSelectQuery(entityFilter: A => LogicalBoolean): Query[B]

        /**
          * Processes results of the select query.
          * @param results The results to process.
          * @return Entities based on the selection results.
          */
        protected def processSelectResults(results: Seq[B]): Seq[A]

        protected def wrapInTransaction[C](body: => C) = {
            schema.wrapInTransaction(body)
        }
    }

    trait NamedEntityTableRepository[A <: PersistableEntity with NamedEntity]
        extends NamedEntityRepository[A]
    {
        self: TableRepository[A, _] =>

        def getByName(name: String): Option[A] = selectOneWhere(_.name === name)
    }

    trait OptionallyOwnedEntityTableRepository[A <: PersistableEntity with OptionallyOwnedEntity]
        extends OptionallyOwnedEntityRepository[A]
    {
        self: TableRepository[A, _] =>

        def getAllByOwnerId(ownerId: Option[String]): Seq[A] = selectWhere(_.owner.map(_.id) === ownerId)
    }

    trait ShareableEntityTableRepository[A <: PersistableEntity with ShareableEntity with OptionallyOwnedEntity]
        extends OptionallyOwnedEntityTableRepository[A]
        with ShareableEntityRepository[A]
    {
        self: TableRepository[A, _] =>

        def getAllPublic: Seq[A] = selectWhere(_.isPublic === true)
    }

    /**
      * A repository that doesn't use any special select query for entity selection. No related entities are selected,
      * therefore it's called lazy.
      */
    class LazyTableRepository[A <: PersistableEntity](table: Table[A], entityConverter: EntityConverter[A])
        extends TableRepository[A, A](table, entityConverter)
    {
        protected def getSelectQuery(entityFilter: A => LogicalBoolean): Query[A] = {
            table.where(e => entityFilter(e))
        }

        protected def processSelectResults(results: Seq[A]): Seq[A] = {
            results
        }
    }
}
