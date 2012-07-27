package cz.payola.domain

import cz.payola.domain.entities.OptionallyOwnedEntity
import cz.payola.common

trait Entity extends common.Entity
{
    val id: String = IDGenerator.newId

    /**
      * Invokes the action if the specified sequence contains the item.
      * @param seq The sequence to check.
      * @param item The item to check.
      * @param action The action to perform.
      * @tparam A Type of the sequence item.
      * @return The item or [[scala.None]] if the item isn't present in the sequence.
      */
    protected def ifContains[A](seq: Seq[A], item: A)(action: => Unit): Option[A] = {
        if (seq.contains(item)) {
            action
            Some(item)
        } else {
            None
        }
    }

    protected def addOwnedEntity[A <: OptionallyOwnedEntity](entity: A, ownedEntities: Seq[A], storer: A => Unit) {
        addOwnedEntity(entity, entity.owner, ownedEntities, storer)
    }

    protected def addOwnedEntity[A, B](entity: A, entityOwner: Option[B], ownedEntities: Seq[A], storer: A => Unit) {
        require(entityOwner.exists(_ == this), "The entity owner doesn't correpond to the current entity.")
        addRelatedEntity(entity, ownedEntities, storer)
    }

    protected def addRelatedEntity[A](entity: A, relatedEntities: Seq[A], storer: A => Unit) {
        require(entity != null, "The entity mustn't be null.")
        require(!relatedEntities.contains(entity), "The entity is already there.")
        storer(entity)
    }

    protected def removeRelatedEntity[A](entity: A, relatedEntities: Seq[A], discarder: A => Unit): Option[A] = {
        require(entity != null, "Entity mustn't be null.")
        ifContains[A](relatedEntities, entity) {
            discarder(entity)
        }
    }

    /**
      * Checks conditions that should always hold. If the entity is inconsistent (i.e. one of the invariants doesn't
      * hold), an exception is thrown.
      */
    protected def checkInvariants() {
        require(id != null, "ID of the %s mustn't be null.".format(classNameText))
    }

    /**
      * Checks conditions that should hold after the constructor is invoked. That are conditions that should always hold
      * after the class is instantiated. Should be called as the last statement of subclass constructors.
      */
    protected def checkConstructorPostConditions() {
        checkInvariants()
    }

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
}
