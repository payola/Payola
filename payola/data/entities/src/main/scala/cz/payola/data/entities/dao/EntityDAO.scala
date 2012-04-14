package cz.payola.data.entities.dao

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.entities.schema.PayolaDB._
import org.squeryl.{KeyedEntity, Table, Queryable}

abstract class EntityDAO[A <: KeyedEntity[String]](protected val table: Table[A])
{
    def getById(id: String): Option[A] = {
        try {
            transaction {
                val result = table.where(e => e.id === id)
                if (result.size == 0) {
                    None
                }
                else {
                    Some(result.single)
                }
            }
        }
        catch {
            case _ => None
        }
    }

    def persist(entity: A) =  {
        transaction {
            if (entity.isPersisted) {
                table.update(entity)
            }
            else {
                table.insert(entity)
            }
        }
    }
}
