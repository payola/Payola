package cz.payola.data.entities.dao

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.entities.schema.PayolaDB._
import org.squeryl.{KeyedEntity, Table, Query}
import collection.mutable.ArrayBuffer

abstract class EntityDAO[A <: KeyedEntity[String]](protected val table: Table[A])
{
    def getById(id: String): Option[A] = {
        // Find entity with specified id
        evaluateSingleResultQuery(table.where(e => e.id === id))
    }

    def removeById(id: String) {
        try
        {
            transaction {
                val result = table.deleteWhere(e => id === e.id)

                // TODO:
                println("delete result " + result)
            }
        }
        catch {
            //TODO: Handle exceptions
            case e : Exception => println("Removing error: " + e)
        }
    }

    def getAll(offset: Int = 0, count: Int = 0): Seq[A] = {
        // Get all entities from table (paginated)
        evaluateCollectionResultQuery(table, offset, count)
    }

    def persist(entity: A) {
        try {
            // Insert or update entity
            transaction {
                if (getById(entity.id) != None) {
                //if (entity.isPersisted) {
                    val result = table.update(entity)

                    println("Update result: " + result)
                }
                else {
                    val result = table.insert(entity)

                    println("Insert result: " + result)
                }
            }
        }
        catch {
            //TODO: Handle exceptions
            case e : Exception => println("Persistance error: " + e)
        }
    }

    protected final def evaluateSingleResultQuery(query: Query[A]): Option[A] = {
        // Find single result an return its Option
        try
        {
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
            case e : Exception => println("Single result query error: " + e)
            None
        }
    }

    protected final def evaluateCollectionResultQuery(
            query : Query[A],
            offset: Int = 0,
            count: Int = 0): Seq[A]  = {

        require(offset >= 0, "Offset must be >= 0")
        require(count >=0 , "Count must be >= 0")

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
            case e : Exception => println("Collection result query error: " + e)
            Seq()
        }
    }


}
