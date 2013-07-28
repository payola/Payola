package cz.payola.domain.entities

import cz.payola.domain._

/**
 * @author Ondřej Heřmánek (ondra.hermanek@gmail.com)
 */
class Prefix(
    protected var _name: String,
    protected override var _prefix: String,
    protected override var _url: String,
    protected override var _owner: Option[User],
    override val id: String = IDGenerator.newId)
    extends Entity
    with OptionallyOwnedEntity
    with NamedEntity
    with cz.payola.common.entities.Prefix
{
    checkConstructorPostConditions()

    /**
      * Sets the owner of the prefix.
      * @param value The new owner of the prefix.
      */
    override def owner_=(value: Option[UserType]) {
        _owner = value
        super[OptionallyOwnedEntity].checkInvariants()
    }

    override final def canEqual(other: Any): Boolean = {
        other.isInstanceOf[Prefix]
    }

    override protected final def checkInvariants() {
        super[Entity].checkInvariants()
        super[NamedEntity].checkInvariants()
        super[OptionallyOwnedEntity].checkInvariants()

        validate(url.length > 0, "url", "URL has to be specified")
        validate(prefix.length > 0, "prefix", "Prefix has to be specified")
    }
}
