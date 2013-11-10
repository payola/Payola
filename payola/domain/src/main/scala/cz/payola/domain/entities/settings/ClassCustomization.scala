package cz.payola.domain.entities.settings

import cz.payola.domain._
import scala.collection.immutable
import cz.payola.common.visual.Color
import cz.payola.common.ValidationException

class ClassCustomization(
    val uri: String,
    protected var _fillColor: String,
    protected var _radius: Int,
    protected var _glyph: String,
    protected var _labels: String,
    protected var _conditionalValue: String,
    protected var _propertyCustomizations: immutable.Seq[PropertyCustomization],
    protected var _orderNum: Int,
    override val id: String = IDGenerator.newId)
    extends Entity
    with cz.payola.common.entities.settings.ClassCustomization
{
    type PropertyCustomizationType = PropertyCustomization

    checkConstructorPostConditions()

    /**
     * Validates that stored value represents a color. Value is stored without spaces.
     * @param value New value of the fill color.
     */
    override def fillColor_=(value: String) {
        validate(value.length == 0 || Color(value).isDefined, "fillColor", "Value doesn't represent any color")
        super.fillColor = value.replace(" ", "")
    }

    def appendPropertyCustomization(propertyCust: PropertyCustomization) {
        _propertyCustomizations = _propertyCustomizations ++ Seq(propertyCust)
    }
}
