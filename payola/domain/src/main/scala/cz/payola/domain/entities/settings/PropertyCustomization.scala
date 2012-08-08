package cz.payola.domain.entities.settings

import cz.payola.domain.Entity
import cz.payola.common.ValidationException
import cz.payola.common.visual.Color

class PropertyCustomization(val uri: String, protected var _strokeColor: String, protected var _strokeWidth: Int)
    extends Entity
    with cz.payola.common.entities.settings.PropertyCustomization
{
    checkConstructorPostConditions()

    /**
     * Validates that stored value represents a color. Value is stored without spaces.
     * @param value New value of the stroke color.
     */
    override def strokeColor_=(value: String) {
        validate(value.length == 0 || Color(value).isDefined, "strokeColor", "Value doesn't represent any color")
        super.strokeColor = value.replace(" ", "")
    }
}
