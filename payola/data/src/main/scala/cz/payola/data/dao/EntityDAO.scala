package cz.payola.data.dao

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{KeyedEntity, Table, Query}
import collection.mutable.ArrayBuffer

abstract class EntityDAO[A <: KeyedEntity[String]](protected val table: Table[A])
{
    def getById(id: String): Option[A] = {
        // Find entity with specified id
        evaluateSingleResultQuery(table.where(e => e.id === id))
    }

    def removeById(id: String): Boolean = {
        try {
            transaction {
                val result = table.deleteWhere(e => id === e.id)

                result == 1
            }
        }
        catch {
            //TODO: Handle exceptions
            case e: Exception => println("Removing error: " + e)

            false
        }
    }

    def getAll(offset: Int = 0, count: Int = 0): Seq[A] = {
        // Get all entities from table (paginated)
        evaluateCollectionResultQuery(table, offset, count)
    }

    protected def persist(entity: A): Option[A] = {
        try {
            // Insert or update entity {
            transaction {
                if (getById(entity.id) != None) {
                    table.update(entity)

                    Some(entity)
                }
                else {
                    table.insert(entity)

                    Some(entity)
                }
            }
        }
        catch {
            //TODO: Handle exceptions
            case e: Exception => println("Persistance error: " + e)

            None
        }
    }

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
            //TODO: Handle exceptions
            case e: Exception => println("Single result query error: " + e)
            None
        }
    }

    protected final def evaluateCollectionResultQuery(
        query: Query[A],
        offset: Int = 0,
        count: Int = 0): Seq[A] = {
        require(offset >= 0, "Offset must be >= 0")
        require(count >= 0, "Count must be >= 0")

        // Get all entities or paginate
        val q = if (offset == 0 && count == 0) query else query.page(offset, count)

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
            //TODO: Handle exceptions
            case e: Exception => println("Collection result query error: " + e)
            Seq()
        }
    }
}
