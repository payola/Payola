package cz.payola.domain.entities.settings

import cz.payola.domain.Entity
import scala.collection.mutable.ListBuffer
import scala.collection.immutable

class ClassCustomization(
    val uri: String,
    protected var _fillColor: String,
    protected var _radius: Int,
    protected var _glyph: String,
    protected var _propertyCustomizations: immutable.Seq[PropertyCustomization])
    extends Entity
    with cz.payola.common.entities.settings.ClassCustomization
{
    checkConstructorPostConditions()

    type PropertyCustomizationType = PropertyCustomization

    def entityTypeName = "ontology class customization"
}
