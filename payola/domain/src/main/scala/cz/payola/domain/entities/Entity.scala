package cz.payola.domain.entities

import cz.payola.domain.IDGenerator

abstract class Entity(val id: String = IDGenerator.newId) extends cz.payola.common.entities.Entity
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

    override def hashCode: Int = {
        id.hashCode
    }

    /**
      * Checks conditions that should always hold. If the entity is inconsistent (i.e. one of the invariants doesn't
      * hold), an exception is thrown.
      */
    protected def checkInvariants() {
        require(id != null, "ID of the entity mustn't be null.")
    }

    /**
      * Checks conditions that should hold after the constructor is invoked. That are conditions that should always hold
      * after the class is instantiated. Should be called as the last statement of subclass constructors.
      */
    protected def checkConstructorPostConditions() {
        checkInvariants()
    }
}
