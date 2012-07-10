package cz.payola.web.client.views.plugins.visual.settings.components.visualsetup

import cz.payola.web.client.mvvm.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.Element
import cz.payola.web.client.events.{EventArgs, ComponentEvent}
import cz.payola.web.client.views.plugins.visual.settings.VertexSettingsModel
import cz.payola.web.client.mvvm.element.{Label, Input, Div}
import cz.payola.web.client.mvvm.element.extensions.Bootstrap.Modal

class VertexModal(model: VertexSettingsModel) extends Component
{
    val settingsChanged = new ComponentEvent[VertexModal, EventArgs[VertexModal]]

    //TODO add some bounds check for every input
    val radius = new Input("vertex.radius", model.radius.toString(), Some("0 ≤ x ≤ 15"))
    val rLabel = new Label("Corner radius [px]:", radius.getDomElement)
    radius.changed += { event =>
        model.radius = radius.getText.toInt
        false
    }

    val color = new ColorPane("vertex.color","Vertex color",model.color)
    color.changed += { event =>
        model.color = color.getColor
        false
    }

    val colorSelected = new ColorPane("vertex.color.selected","Vertex color (selected)",model.colorSelected)
    colorSelected.changed += { event =>
        model.colorSelected = colorSelected.getColor
        false
    }

    val wrapper = new Div(List(rLabel, radius, color, colorSelected))

    private val modal = new Modal("Vertex settings", List(wrapper))

    modal.saved += {
        event =>
            settingsChanged.trigger(new EventArgs[VertexModal](this))
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