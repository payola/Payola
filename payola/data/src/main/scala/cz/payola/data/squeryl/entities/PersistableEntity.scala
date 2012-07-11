package cz.payola.data.squeryl.entities

import org.squeryl.{Query, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._
import collection.mutable.ArrayBuffer
import org.squeryl.dsl.{ManyToOne, OneToMany, ManyToMany}
import cz.payola.data.DataException
import cz.payola.domain._
import cz.payola.domain
import cz.payola.data.squeryl.SquerylDataContextComponent

/**
  * This trait provided persistance to entities and allows them to create relations with entities
  * (if relation is defined in [[cz.payola.data.PayolaDB]] schema)
  *
  */
trait PersistableEntity extends cz.payola.domain.Entity with KeyedEntity[String]
{
    val context: SquerylDataContextComponent

    protected def wrapInTransaction[C](body: => C) = {
        context.schema.wrapInTransaction(body)
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
    protected final def associate[A <: PersistableEntity](entity: A, relation: ManyToMany[A,_]): A = wrapInTransaction {
        if (relation.find(_.id == entity.id).isEmpty) {
            relation.associate(entity)
        }
        entity
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
    protected final def dissociate[A <: PersistableEntity](entity: A, relation: ManyToMany[A,_]): A = wrapInTransaction {
        if (relation.find(_.id == entity.id).isDefined) {
            relation.dissociate(entity)
        }
        entity
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
    protected final def associate[A <: PersistableEntity](entity: A, relation: OneToMany[A]): A = wrapInTransaction {
        if (relation.find(e => e.id == entity.id).isEmpty) {
            relation.associate(entity)
        }
        entity
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
            case that: domain.Entity => that.canEqual(this) && this.id == that.id
            case _ => false
        }
    }
}
