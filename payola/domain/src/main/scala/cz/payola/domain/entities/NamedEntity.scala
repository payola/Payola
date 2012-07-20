package cz.payola.domain.entities

import cz.payola.domain.Entity

trait NamedEntity extends cz.payola.common.entities.NamedEntity
{
    self: Entity =>

    override def name_=(value: String) {
        check(value)
        _name = value
    }

    protected def checkInvariants() {
        check(name)
    }

    private def check(nameToCheck: String) {
        validate(nameToCheck != null, "name", "Name of the %s mustn't be null.".format(entityTypeName))
        validate(nameToCheck.trim != "", "name", "Name of the %s mustn't be empty.".format(entityTypeName))
    }
}
