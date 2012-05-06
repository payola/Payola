package cz.payola.web.client.views.plugins.visual.components.visualsetup

import s2js.adapters.js.browser.document
import collection.mutable.ArrayBuffer
import cz.payola.web.client.mvvm_api.Component
import cz.payola.web.client.views.plugins.visual.Color
import cz.payola.web.client.mvvm_api.element.Input
import s2js.adapters.js.dom.{Node, Element}
import cz.payola.web.client.events.{ChangedEvent, ChangedEventArgs}

/**
 *
 * @author jirihelmich
 * @created 5/3/12 9:44 AM
 * @package cz.payola.web.client.views.plugins.visual.components.visualsetup
 */

class ColorPane(name: String, var color: Color) extends Component
{
    val changed = new ChangedEvent[ColorPane]

    val colorInput: Input = new Input(name, color.toHexString, "colorpicker")
    colorInput.changed += { event =>
        changed.trigger(new ChangedEventArgs(this))
        false
    }
    colorInput.clicked += { event =>
        false
    }

    def render(parent: Element) {
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
