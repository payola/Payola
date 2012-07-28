package cz.payola.domain.entities

import cz.payola.domain.Entity

trait NamedEntity extends Entity with cz.payola.common.entities.NamedEntity
{
    override def name_=(value: String) {
        check(value)
        _name = value
    }

    protected override def checkInvariants() {
        check(name)
    }

    private def check(nameToCheck: String) {
        validate(nameToCheck != null, "name", "Name of the %s mustn't be null.".format(classNameText))
        validate(nameToCheck.trim != "", "name", "Name of the %s mustn't be empty.".format(classNameText))
    }
}
