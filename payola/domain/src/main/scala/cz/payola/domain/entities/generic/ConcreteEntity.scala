package cz.payola.domain.entities.generic

import cz.payola.common.entities.Entity

/** Defines a concrete entity which is the root class of all entities at the domain level.
  *
  * @param id Entity ID.
  */
class ConcreteEntity(override val id: String) extends Entity {

    /** Redefining equals to match the ID of both entities and their classes.
      *
      * @param other The other object to compare to.
      * @return True if both objects are of the same class and their IDs match.
      */
    override def equals(other: Any): Boolean = {
        other match {
            case entity: ConcreteEntity => {
                this.getClass == entity.getClass && this.id == entity.id
            }
            case _ => false;
        }
    }

    /** A hash code that uses the object's ID and its class to compute hash code.
      *
      * It's important to include the class name as two objects may have the same ID,
      * yet be of a different class. Could result in collisions and what not.
      *
      * @return Hash code of this entity object.
      */
    override def hashCode = 333 * (id.hashCode + 666) * this.getClass.getName.hashCode

}

