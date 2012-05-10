package cz.payola.domain.entities

import cz.payola.domain.IDGenerator

class Entity(val id: String = IDGenerator.newId) extends cz.payola.common.entities.Entity
{
    def canEqual(other: Any): Boolean = {
        other.isInstanceOf[Entity]
    }

    override def equals(other: Any): Boolean = {
        other match {
            case that: Entity => that.canEqual(this) && this.id == that.id
            case _ => false
        }
    }

    override def hashCode: Int = id.hashCode
}
