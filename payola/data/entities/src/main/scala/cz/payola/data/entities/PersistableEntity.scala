package cz.payola.data.entities

import org.squeryl.{Query, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._
import collection.mutable.ArrayBuffer

trait PersistableEntity extends KeyedEntity[String]
{

    protected final def evaluateCollection[A](col: Query[A]): collection.Seq[A]  = {
        transaction {
            val entities: ArrayBuffer[A] = new ArrayBuffer[A]()

            for (e <- col) {
                entities += e
            }

            entities.toSeq
        }
    }
}
