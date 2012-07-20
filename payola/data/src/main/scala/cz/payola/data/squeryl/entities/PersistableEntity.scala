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
trait PersistableEntity extends KeyedEntity[String]
{
    self: cz.payola.domain.Entity =>

    val context: SquerylDataContextComponent

    protected def wrapInTransaction[C](body: => C) = {
        context.schema.wrapInTransaction(body)
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
