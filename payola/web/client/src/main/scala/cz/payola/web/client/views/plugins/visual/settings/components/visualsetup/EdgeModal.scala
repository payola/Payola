package cz.payola.web.client.views.plugins.visual.settings.components.visualsetup

import s2js.adapters.js.browser.document
import s2js.adapters.js.dom._
import cz.payola.web.client.views.Component
import cz.payola.web.client.views.events._
import cz.payola.web.client.views.plugins.visual.settings.EdgeSettingsModel
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.extensions.bootstrap.Modal
import cz.payola.web.client.events._
import cz.payola.web.client.views.elements.Input
import cz.payola.web.client.views.elements.Div

class EdgeModal(model: EdgeSettingsModel) extends Component
{
    val settingsChanged = new SimpleEvent[EdgeModal]

    val width = new Input("edge.width", model.width.toString(), None)
    val wLabel = new Label("Width [px]:", width)
    width.changed += { event =>
        model.width = width.value.toInt
        false
    }

    val straightenIndex = new Input("edge.straigthtenIndex", model.straightenIndex.toString(), None)
    val sLabel = new Label("Straighten index:", straightenIndex)
    straightenIndex.changed += { event =>
        model.straightenIndex = straightenIndex.value.toInt
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
        event => settingsChanged.triggerDirectly(this)
    }

    def render(parent: Node) {
        modal.render(parent)
    }

    def show() {
        modal.show
    }

    def hide() {
        modal.hide
    }

    def domElement : Element = modal.domElement

    def destroy() {
        // TODO
    }
}
