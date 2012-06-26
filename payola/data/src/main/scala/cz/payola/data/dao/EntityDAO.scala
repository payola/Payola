package cz.payola.data.dao

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{KeyedEntity, Table, Query}
import collection.mutable.ArrayBuffer

abstract class EntityDAO[A <: KeyedEntity[String]](protected val table: Table[A])
{
    /**
      * Searches table of entities for entity with specified id
      *
      * @param id - id of an entity to search for
      * @return Returns Some(entity) if is found, None otherwise
      */
    def getById(id: String): Option[A] = {
        // Find entity with specified id
        evaluateSingleResultQuery(table.where(e => e.id === id))
    }

    /**
      * Searches table for an entity with specified id.
      * If entity is found will be removed.
      *
      * @param id - id of an entity to remove
      * @return Returns true if entity is removed, false otherwise
      */
    def removeById(id: String): Boolean = {
        try {
            transaction {
                val result = table.deleteWhere(e => id === e.id)

                result == 1
            }
        }
        catch {
            case e: Exception => {println("Removing error: " + e); throw e;}
        }
    }

    /**
      * Returns all entities from table. Result may be paginated.
      *
      * @param pagination - Optionally specified pagination
      * @return Returns all specified entities
      */
    def getAll(pagination: Option[PaginationInfo] = None): Seq[A] = {
        // Get all entities from table (paginated)
        evaluateCollectionResultQuery(table, pagination)
    }

    /**
      * Inserts/updates given entity to/in the table
      *
      * @param entity - Entity to persist
      * @return Returns persisted entity
      */
    protected def persist(entity: A): A = {
        try {
            // Insert or update entity {
            transaction {
                if (getById(entity.id) != None) {
                    table.update(entity)
                }
                else {
                    table.insert(entity)
                }

                // Return entity
                entity
            }
        }
        catch {
            case e: Exception => {println("Persistance error: " + e); throw e}
        }
    }

    /**
      * Evaluates specified query and returns only the first entity in the result.
      *
      * @param query - query with 0 or 1 entitiy in result
      * @return Retuns Option of the first entity in result, None if result is empty
      */
    protected final def evaluateSingleResultQuery(query: Query[A]): Option[A] = {
        // Find single result an return its Option
        try {
            transaction {
                if (query.size == 0) {
                    None
                }
                else {
                    Some(query.single)
                }
            }
        }
        catch {
            case e: Exception => {println("Single-result query error: " + e); throw e;}
        }
    }

    /**
      * Evaluated specified query that returns collection of entities as a result.
      * Result may be paginated.
      *
      * @param query - query to evaluate
      * @param pagination - Optionally specified pagination of the query
      * @return Returns collection of entities as a result of the query
      */
    protected final def evaluateCollectionResultQuery(query: Query[A], pagination: Option[PaginationInfo]) = {

        // Get all entities or paginate
        val q = pagination.map(p => query.page(p.skip, p.limit)).getOrElse(query)

        try {
            transaction {
                val entities: ArrayBuffer[A] = new ArrayBuffer[A]()

                for (e <- q) {
                    entities += e
                }

                entities.toSeq
            }
        }
        catch {
            case e: Exception => {println("Collection-result query error: " + e); throw e;}
        }
    }
}
