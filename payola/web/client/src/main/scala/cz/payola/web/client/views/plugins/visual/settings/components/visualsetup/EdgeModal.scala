package cz.payola.web.client.views.plugins.visual.settings.components.visualsetup

import cz.payola.web.client.mvvm.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.Element
import cz.payola.web.client.events.{EventArgs, ComponentEvent}
import cz.payola.web.client.views.plugins.visual.settings.EdgeSettingsModel
import cz.payola.web.client.mvvm.element.{Label, Input, Div}
import cz.payola.web.client.mvvm.element.extensions.Bootstrap.Modal

class EdgeModal(model: EdgeSettingsModel) extends Component
{
    val settingsChanged = new ComponentEvent[EdgeModal, EventArgs[EdgeModal]]

    val width = new Input("edge.width", model.width.toString(), None)
    val wLabel = new Label("Width [px]:", width.field)
    width.changed += { event =>
        model.width = width.getText.toInt
        false
    }

    val straightenIndex = new Input("edge.straigthtenIndex", model.straightenIndex.toString(), None)
    val sLabel = new Label("Straighten index:", straightenIndex.field)
    straightenIndex.changed += { event =>
        model.straightenIndex = straightenIndex.getText.toInt
        false
    }

    val colorSelect = new ColorPane("edge.color.select", "Edge color (selected)",model.colorSelected)
    colorSelect.changed += { event =>
        model.colorSelected = colorSelect.getColor
        false
    }

    val color = new ColorPane("edge.color.base", "Edge color", model.color)
    color.changed += { event =>
        model.color = color.getColor
        false
    }

    val wrapper = new Div(
        List(wLabel, width, sLabel, straightenIndex, colorSelect, color))

    private val modal = new Modal("Edge settings", List(wrapper))

    modal.saved += {
        event => settingsChanged.trigger(new EventArgs[EdgeModal](this))
    }

    def render(parent: Element = document.body) {
        modal.render(parent)
    }

    def show() {
        modal.show
    }

    def hide() {
        modal.hide
    }

    def getDomElement : Element = modal.getDomElement
}
