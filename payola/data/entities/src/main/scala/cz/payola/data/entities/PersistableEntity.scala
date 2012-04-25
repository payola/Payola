package cz.payola.data.entities

import org.squeryl.{Query, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._
import collection.mutable.ArrayBuffer
import org.squeryl.dsl.{ManyToOne, OneToMany, ManyToMany}

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

    protected final def associate[A <: PersistableEntity](entity: A, relation: ManyToMany[A,_]) {
        transaction {
            if (relation.find(e => e.id == entity.id) == None) {
                relation.associate(entity)
            }
        }
    }

    protected final def assign[A <: PersistableEntity](entity: A, relation: ManyToMany[A,_]) {
        transaction {
            if (relation.find(e => e.id == entity.id) == None) {
                relation.assign(entity)
            }
        }
    }

    protected final def dissociate[A <: PersistableEntity](entity: A, relation: ManyToMany[A,_]) {
        transaction {
            if (relation.find(e => e.id == entity.id) != None) {
                relation.dissociate(entity)
            }
        }
    }

    protected final def associate[A <: PersistableEntity](entity: A, relation: OneToMany[A]) {
        transaction {
            if (relation.find(e => e.id == entity.id) == None) {
                relation.associate(entity)
            }
        }
    }

    /*
    protected final def assign[A <: PersistableEntity](entity: A, relation: OneToMany[A]) {
        transaction {
            if (relation.find(e => e.id == entity.id) == None) {
                relation.assign(entity)
            }
        }
    }
    protected final def assign[A <: PersistableEntity](entity: A, relation: ManyToOne[A]) {
        transaction {
            if (relation.find(e => e.id == entity.id) == None) {
                relation.assign(entity)
            }
        }
    }
    */
}
