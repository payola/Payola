package cz.payola.web.client.views.plugins.visual.components.visualsetup

import s2js.adapters.js.browser.document
import collection.mutable.ArrayBuffer
import cz.payola.web.client.events.{ChangedEvent}
import cz.payola.web.client.mvvm_api.Component
import cz.payola.web.client.views.plugins.visual.Color
import cz.payola.web.client.mvvm_api.element.Input
import s2js.adapters.js.dom.{Node, Element}

/**
 *
 * @author jirihelmich
 * @created 5/3/12 9:44 AM
 * @package cz.payola.web.client.views.plugins.visual.components.visualsetup
 */

class ColorPane(namePrefix: String, var color: Color) extends Component
{
    val changed = new ArrayBuffer[ChangedEvent[ColorPane] => Boolean]()

    val colorInput: Input = new Input(namePrefix+"_color", color.toHexString, "colorpicker")
    colorInput.changed += { event =>
        notify(changed, new ChangedEvent[ColorPane](this))
        false
    }

    def render(parent: Node) {
        colorInput.render(parent)
    }

    def getColor : Color = {
        color
    }

    def setColor(value: Color) {
        color = value
        colorInput.setText(value.toHexString)
    }

}
