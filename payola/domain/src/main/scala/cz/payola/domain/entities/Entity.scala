package cz.payola.domain.entities

import cz.payola.domain.IDGenerator

class Entity(val id: String = IDGenerator.newId) extends cz.payola.common.entities.Entity
{
    def canEqual(other: Any): Boolean = {
        // Just checking for isInstanceOf[Entity] isn't sufficient, as otherwise,
        // equals would return true when an Analysis and a User of the same ID were
        // compared, which isn't correct

        // TODO - this, however, yields another problem: when extending
        // the class on the fiy (e.g. new User("My Name') with SomeTrait), the matching
        // will be gone, as the object's class will be Something$$anonfun$1$$anon$1

        other.isInstanceOf[Entity] && (other.getClass == this.getClass)
    }

    override def equals(other: Any): Boolean = {
        other match {
            case that: Entity => that.canEqual(this) && this.id == that.id
            case _ => false
        }
    }

    // TODO
    // Not sure it's a good idea to just return ID's hashCode - personally,
    // I think it should be or'ed or combined with something else - like
    // it was before with the class' name.
    override def hashCode: Int = id.hashCode
}
