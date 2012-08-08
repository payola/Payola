package cz.payola.data.squeryl

import org.squeryl.KeyedEntity

/**
 * This trait provided persistence to entities from domain layer, allows them to create relations with other entities.
 * This relation must be defined in [[cz.payola.data.squeryl.SchemaComponent]] schema.
 *
 */
trait Entity extends KeyedEntity[String] with cz.payola.domain.Entity
{
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
            case that: cz.payola.domain.Entity => that.canEqual(this) && this.id == that.id
            case _ => false
        }
    }
}
