package cz.payola.data.entities.dao

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.entities.PayolaDB._
import org.squeryl.{KeyedEntity, Table, Queryable}

abstract class EntityDAO[A <: KeyedEntity[_]](protected val table: Table[A])
{
    def getById(id: String): Option[A] = {
        /*
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
        */
        None
    }

    /*
    def persist(entity: A) = {
        if (entity.isPersisted) {
            entity.update
        }
        else {
            entity.save
        }
    }
    */
}
