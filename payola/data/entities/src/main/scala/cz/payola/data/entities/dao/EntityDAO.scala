package cz.payola.data.entities.dao

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.entities.schema.PayolaDB._
import org.squeryl.{KeyedEntity, Table, Query}
import collection.mutable.ArrayBuffer

abstract class EntityDAO[A <: KeyedEntity[String]](protected val table: Table[A])
{
    def getById(id: String): Option[A] = {
        try {
            transaction {
                table.lookup(id)
            }
        }
        catch {
            // TODO: Handle exceptions
            case _ => None
        }
    }

    def getAll(offset: Int = 0, count: Int = 0): Seq[A] = {
        require(offset >= 0, "Offset must be >= 0")
        require(count >=0 , "Count must be >= 0")

        val query = if (offset == 0 && count == 0) from(table)(select (_)) else table.page(offset, count)

        transaction {
            val entities: ArrayBuffer[A] = new ArrayBuffer[A]()

            for (e <- query) {
                entities += e
            }

            entities.toSeq
        }
    }

    protected final def evaluateSingleResultQuery(query: Query[A]): Option[A] = {
        transaction {
            if (query.size == 0) {
                None
            }
            else {
                Some(query.single)
            }
        }
    }

    def persist(entity: A) =  {
        try {
            transaction {
                if (entity.isPersisted) {
                    table.update(entity)
                }
                else {
                    table.insert(entity)
                }
            }
        }
        catch {
            //TODO: Handle exceptions
            case e : Exception => println("Persistance exrror: " + e)
        }
    }
}
