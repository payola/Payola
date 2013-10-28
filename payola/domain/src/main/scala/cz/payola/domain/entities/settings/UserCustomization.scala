package cz.payola.domain.entities.settings

import cz.payola.domain._
import cz.payola.domain.entities._
import scala.collection.immutable

object UserCustomization
{
    /**
     * Crates an empty user customization.
     * @param name Name of the customization.
     * @param owner Owner of the customization.
     * @return The customization.
     */
    def empty(name: String, owner: Option[User]): UserCustomization = {

        new UserCustomization(IDGenerator.newId,
            "http://user_"+owner.get.id+"/"+owner.get.name+"/"+name, name, owner, List[ClassCustomization]())
    }
}

class UserCustomization(
    override val id: String,
    val URLs: String,
    protected var _name: String,
    protected var _owner: Option[User],
    protected var _classCustomizations: immutable.Seq[ClassCustomization])
    extends Entity
    with NamedEntity
    with OptionallyOwnedEntity
    with cz.payola.common.entities.settings.UserCustomization
{
    checkConstructorPostConditions()

    type ClassCustomizationType = ClassCustomization

    def appendClassCustomization(classCust: ClassCustomization) {
        _classCustomizations = _classCustomizations ++ Seq(classCust)
    }

    override def canEqual(other: Any): Boolean = {
        other.isInstanceOf[UserCustomization]
    }

    override protected def checkInvariants() {
        super[Entity].checkInvariants()
        super[NamedEntity].checkInvariants()
        super[OptionallyOwnedEntity].checkInvariants()
    }
}
