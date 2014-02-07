package cz.payola.data.squeryl

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.ast.LogicalBoolean
import cz.payola.data.PaginationInfo
import cz.payola.data.squeryl.entities._
import cz.payola.domain.entities.NamedEntity

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
    abstract class TableRepository[A <: Entity, B](val table: Table[A],
        val entityConverter: EntityConverter[A])
        extends Repository[A]
    {
        def getByIds(ids: Seq[String]): Seq[A] = {
            selectWhere(entity => entity.id in ids)
        }

        def getAll(pagination: Option[PaginationInfo] = None): Seq[A] = {
            selectWhere(_ => 1 === 1, pagination)
        }

        def removeById(id: String): Boolean = wrapInTransaction {
            table.deleteWhere(e => id === e.id) == 1
        }

        def persist(entity: AnyRef): A = wrapInTransaction {
            val convertedEntity = entityConverter(entity)
            persist(convertedEntity, table)
            convertedEntity
        }

        def getCount: Long = wrapInTransaction {
            from(table)(e => compute(count))
        }

        /**
         * Selects all entities that pass the specified entity filter.
         * @param entityFilter A filter that excludes entities from the result.
         * @return The selected entities.
         */
        private[squeryl] def selectWhere(entityFilter: A => LogicalBoolean, pagination: Option[PaginationInfo] = None):
        Seq[A] = wrapInTransaction {
            // Define select query
            val query = select(getSelectQuery(entityFilter))
            // Simple pagination
            pagination.map(p => query.drop(p.skip).take(p.limit)).getOrElse(query)
        }

        /**
         * Selects the first entity that passes the specified entity filter.
         * @param entityFilter A filter that excludes entities from the result.
         * @return The selected entity.
         */
        private[squeryl] def selectOneWhere(entityFilter: A => LogicalBoolean): Option[A] = {
            selectWhere(entityFilter).headOption
        }

        /**
         * Executes the specified query and returns its results.
         * @param query The query to execute.
         * @return Results of the query.
         */
        protected def select(query: Query[B]): Seq[A] = schema.wrapInTransaction {
            processSelectResults(query.toList)
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

        /**
         * Persists the specified entity to the specified table.
         * @param entity The entity to persist.
         * @param table Tha table to persist the entity int.
         * @tparam C Type of the entity.
         */
        protected def persist[C <: Entity](entity: C, table: Table[C]) {
            schema.persist(entity, table)
        }

        /**
         * Wraps code block into inTransaction block.
         * All commands that acces data vis Squeryl needs to be wrapped in inTransaction block.
         *
         * @param body Code block to be wrapped
         * @tparam C Return type of the code block
         * @return Returns result of the code block
         */
        protected def wrapInTransaction[C](body: => C) = {
            schema.wrapInTransaction(body)
        }
    }

    /**
     * Repository that fetches entities with name
     * @tparam A Type of the named entities in the repository.
     */
    trait NamedEntityTableRepository[A <: Entity with NamedEntity]
        extends NamedEntityRepository[A]
    {
        self: TableRepository[A, _] =>
        def getByName(name: String): Option[A] = selectOneWhere(_.name === name)
    }

    /**
     * Repository, that fetches entities with their optional owner
     * @tparam A Type of the optionally owned entities in the repository.
     * @tparam B Result type of database query, when entity is load, in most cases it is type [(A, Option[User])]
     */
    trait OptionallyOwnedEntityTableRepository[A <: Entity with OptionallyOwnedEntity with NamedEntity, B]
        extends OptionallyOwnedEntityRepository[A]
    {
        self: TableRepository[A, B] =>
        def getAllByOwnerId(ownerId: Option[String]): Seq[A] = {
            selectWhere(_.ownerId === ownerId).sortBy(_.name)
        }
    }

    /**
     * Repository that fetches shareable entities
     * @tparam A Type of the shareable entities in the repository.
     * @tparam B Result type of database query, when entity is load, in most cases it is type [(A, Option[User])]
     */
    trait ShareableEntityTableRepository[A <: Entity
        with ShareableEntity with OptionallyOwnedEntity with NamedEntity, B]
        extends OptionallyOwnedEntityTableRepository[A, B]
        with ShareableEntityRepository[A]
    {
        self: TableRepository[A, B] =>
        def getAllPublic(forListing: Boolean = false): Seq[A] = {
            if (forListing){
                selectWhere{ e => e.isPublic === true and e.isVisibleInListings === true }.sortBy(_.name)
            } else {
                selectWhere(_.isPublic === true).sortBy(_.name)
            }
        }
    }

    /**
     * A repository that doesn't use any special select query for entity selection. No related entities are selected,
     * therefore it's called lazy.
     */
    class LazyTableRepository[A <: Entity](table: Table[A], entityConverter: EntityConverter[A])
        extends TableRepository[A, A](table, entityConverter)
    {
        protected def getSelectQuery(entityFilter: A => LogicalBoolean): Query[A] = {
            table.where(e => entityFilter(e))
        }

        protected def processSelectResults(results: Seq[A]): Seq[A] = {
            results
        }
    }

    /**
     * A repository that loads entity with its owner
     * @param table The corresponding table with the entities.
     * @param entityConverter A converter that converts to instances that can be stored into the table.
     * @tparam A Type of the entities in the repository.
     */
    class OptionallyOwnedEntityDefaultTableRepository[A <: Entity with OptionallyOwnedEntity with NamedEntity](
        table: Table[A], entityConverter: EntityConverter[A])
        extends TableRepository[A, (A, Option[User])](table, entityConverter)
        with OptionallyOwnedEntityTableRepository[A, (A, Option[User])]
    {
        protected def getSelectQuery(entityFilter: A => LogicalBoolean): Query[(A, Option[User])] = {
            join(table, schema.users.leftOuter)((e, o) =>
                where(entityFilter(e))
                    select(e, o)
                    on (e.ownerId === o.map(_.id))
            )
        }

        protected def processSelectResults(results: Seq[(A, Option[User])]): Seq[A] = {
            results.groupBy(_._1).map {r =>
                val entity = r._1
                entity.owner = r._2.head._2

                entity
            }(collection.breakOut)
        }
    }

}
