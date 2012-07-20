package cz.payola.domain.entities.settings

import cz.payola.domain.Entity

class PropertyCustomization(val uri: String, protected var _strokeColor: String, protected var _strokeWidth: Int)
    extends Entity
    with cz.payola.common.entities.settings.PropertyCustomization
{
    checkConstructorPostConditions()

    def entityTypeName = "ontology property customization"
}
