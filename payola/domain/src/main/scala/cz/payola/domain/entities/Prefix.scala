package cz.payola.domain.entities

import cz.payola.domain._

/**
 * @author Ondřej Heřmánek (ondra.hermanek@gmail.com)
 */
class Prefix(
    protected var _name: String,
    protected override var _prefix: String,
    protected override var _url: String,
    override val id: String = IDGenerator.newId)
    extends Entity
    with OptionallyOwnedEntity
    with NamedEntity
    with cz.payola.common.entities.Prefix
{
    // The owner has to be declared before the checkConstructorPostConditions invocation, which verifies it's not null.
    final var _owner: Option[UserType] = None

    checkConstructorPostConditions()

    def shortenUri(uri:String): Option[String] = {
        if (uri.startsWith(url))
            Some(uri.replace(url, "@" + prefix))
        else
            None
    }

    /**
      * Sets the owner of the prefix.
      * @param value The new owner of the prefix.
      */
    final override def owner_=(value: Option[UserType]) {
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

        /* TODO: validate
        validate(inputCount >= 0, "inputCount", "The inputCount of the plugin must be a non-negative number.")
        validate(parameters != null, "parameters", "The parameters of the plugin mustn't be null.")
        validate(!parameters.contains(null), "parameters", "The parameters of the plugin mustn't contain null.")
        */
    }
}
