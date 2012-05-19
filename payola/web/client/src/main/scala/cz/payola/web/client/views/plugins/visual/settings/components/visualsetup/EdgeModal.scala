package cz.payola.web.client.views.plugins.visual.settings.components.visualsetup

import cz.payola.web.client.mvvm_api.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.Element
import cz.payola.web.client.events.{EventArgs, ComponentEvent}
import cz.payola.web.client.views.plugins.visual.settings.EdgeSettingsModel
import cz.payola.web.client.mvvm_api.element.{Label, Input, Div}

class EdgeModal(model: EdgeSettingsModel) extends Component
{
    val settingsChanged = new ComponentEvent[EdgeModal, EventArgs[EdgeModal]]

    val width = new Input("edge.width", model.width.toString())
    val wLabel = new Label("Width [px]:", width.field)

    val straightenIndex = new Input("edge.straigthtenIndex", model.straightenIndex.toString())
    val sLabel = new Label("Straighten index:", straightenIndex.field)

    val colorSelect = new ColorPane("edge.color.select", "Edge color (selected)",model.colorSelected)

    val color = new ColorPane("edge.color.base", "Edge color", model.color)

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
}
