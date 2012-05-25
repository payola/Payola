package cz.payola.domain.entities

trait NamedEntity extends cz.payola.common.entities.NamedEntity
{
    protected def checkInvariants() {
        require(name != null, "Name of the entity mustn't be null.")
    }
}
