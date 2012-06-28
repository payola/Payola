package cz.payola.data.entities

import org.squeryl.{Query, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._
import collection.mutable.ArrayBuffer
import org.squeryl.dsl.{ManyToOne, OneToMany, ManyToMany}
import cz.payola.data.DataException

/**
  * This trait provided persistance to entities and allows them to create relations with entities
  * (if relation is defined in [[cz.payola.data.PayolaDB]] schema)
  *
  */
trait PersistableEntity extends cz.payola.domain.entities.Entity with KeyedEntity[String]
{
    /**
      * Evaluates query that should return a collection of entities as a result.
      *
      * @param query - Query that returns collection
      * @tparam A - type of entities in result
      * @return Returns collection of entities of type A
      */
    protected final def evaluateCollection[A](query: Query[A]): collection.Seq[A] =
        DataException.wrap {
            transaction {
                val entities: ArrayBuffer[A] = new ArrayBuffer[A]()

                for (e <- query) {
                    entities += e
                }

                entities.toSeq
            }
        }

    /**
      * Creates M:N relation of this entity and specified entity.
      * Specified entity will be persisted.
      *
      * @param entity - specified entity that will be related with this entity
      * @param relation - definition of M:N relation
      * @tparam A - type of specified entity
      * @return Returns persisted specified entity
      */
    protected final def associate[A <: PersistableEntity](entity: A, relation: ManyToMany[A,_]): A =
        DataException.wrap {
            transaction {
                if (relation.find(e => e.id == entity.id) == None) {
                    relation.associate(entity)
                }

                // Return entity
                entity
            }
        }

    /**
      * Removes M:N relation between this entity and specified entity.
      * No entity will be removed.
      *
      * @param entity - specified entity whose relation with this item should be removed
      * @param relation - definition on M:N relation
      * @tparam A - type of specified entity
      * @return Returns specified entity
      */
    protected final def dissociate[A <: PersistableEntity](entity: A, relation: ManyToMany[A,_]): A =
        DataException.wrap {
            transaction {
                if (relation.find(e => e.id == entity.id) != None) {
                    relation.dissociate(entity)
                }

                // Return entity
                entity
            }
        }

    /**
      * Creates 1:N relation between this entity (on '1' side of relation) and specified entity (on 'N' side of relation).
      * Specified entity wil be persisted
      *
      * @param entity - specified entity to be ralted with this entity
      * @param relation  - definition of 1:N relation between this and specified entity
      * @tparam A - type of specified entity
      * @return Returns pesisted specified entity
      */
    protected final def associate[A <: PersistableEntity](entity: A, relation: OneToMany[A]): A =
        DataException.wrap {
            transaction {
                if (relation.find(e => e.id == entity.id) == None) {
                    relation.associate(entity)
                }

                // Return entity
                entity
            }
        }

    /**
      * Checks nothing, because SQUERYL fills constructors of Entities
      * with null values during DB schema initialization,
      * so all inner checks would fail during this initialization.
      */
    override protected def checkConstructorPostConditions() {}

    /**
      * This method is a copy of [[cz.payola.domain.entities.Entity.equals]] method.
      */
    override def equals(other: Any): Boolean = {
        other match {
            case that: cz.payola.domain.entities.Entity => that.canEqual(this) && this.id == that.id
            case _ => false
        }
    }
}