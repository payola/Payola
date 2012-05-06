package cz.payola.web.client.views.plugins.visual.components.visualsetup

import cz.payola.web.client.mvvm_api.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.Element
import cz.payola.web.client.mvvm_api.element.{Input, Div}
import cz.payola.web.client.events.{EventArgs, ComponentEvent}
import cz.payola.web.client.views.plugins.visual.VertexSettingsModel

class VertexModal(model: VertexSettingsModel) extends Component
{
    val settingsChanged = new ComponentEvent[VertexModal, EventArgs[VertexModal]]

    val cornerRadius = new Input("vertex.corner.radius", model.cornerRadius.toString())

    val width = new Input("vertex.width", model.width.toString())

    val height = new Input("vertex.height", model.height.toString())

    val colorLow = new ColorPane("vertex.color.low",model.colorLow)

    val colorMed = new ColorPane("vertex.color.medium",model.colorMed)

    val colorHigh = new ColorPane("vertex.color.high",model.colorHigh)

    val colorLiteral = new ColorPane("vertex.color.literal",model.colorLiteral)

    val colorIdentifier = new ColorPane("vertex.color.identifier",model.colorIdentifier)

    val colorUnknown = new ColorPane("vertex.color.unknown",model.colorUnknown)

    val literalIcon = new Input("vertex.icon.literal", model.literalIcon)

    val identifierIcon = new Input("vertex.icon.identifier", model.identifierIcon)

    val unknownIcon = new Input("vertex.icon.unknow", model.unknownIcon)

    val wrapper = new Div(
        List(cornerRadius, width, height, colorLow, colorMed, colorHigh, colorLiteral, colorIdentifier, colorUnknown,
            literalIcon, identifierIcon, unknownIcon))

    private val modal = new Modal("Vertex settings", List(wrapper))

    modal.saved += {
        event => settingsChanged.trigger(new EventArgs[VertexModal](this))
    }

    def render(parent: Element = document.body) = {
        modal.render(parent)
    }

    def show {
        modal.show
    }

    def hide {
        modal.hide
    }
}
