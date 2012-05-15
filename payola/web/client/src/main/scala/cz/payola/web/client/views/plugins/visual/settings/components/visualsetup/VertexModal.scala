package cz.payola.web.client.views.plugins.visual.settings.components.visualsetup

import cz.payola.web.client.mvvm_api.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.Element
import cz.payola.web.client.mvvm_api.element.{Input, Div}
import cz.payola.web.client.events.{EventArgs, ComponentEvent}
import cz.payola.web.client.views.plugins.visual.settings.VertexSettingsModel
import s2js.adapters.js.browser.window

class VertexModal(model: VertexSettingsModel) extends Component
{
    val settingsChanged = new ComponentEvent[VertexModal, EventArgs[VertexModal]]

    val cornerRadius = new Input("vertex.corner.radius", model.cornerRadius.toString())
    cornerRadius.changed += {
        event => model.cornerRadius = cornerRadius.getText.toInt
        window.alert(model.cornerRadius)
        false
    }

    val width = new Input("vertex.width", model.width.toString())
    width.changed += {
        event => model.width = width.getText.toInt
        false
    }

    val height = new Input("vertex.height", model.height.toString())
    height.changed += {
        event => model.height = height.getText.toInt
        false
    }

    val colorLow = new ColorPane("vertex.color.low",model.colorLow)
    colorLow.changed += {
        event => model.colorLow = colorLow.getColor
        false
    }

    val colorMed = new ColorPane("vertex.color.medium",model.colorMed)
    colorMed.changed += {
        event => model.colorMed = colorMed.getColor
        false
    }

    val colorHigh = new ColorPane("vertex.color.high",model.colorHigh)
    colorHigh.changed += {
        event => model.colorHigh = colorHigh.getColor
        false
    }

    val colorLiteral = new ColorPane("vertex.color.literal",model.colorLiteral)
    colorLiteral.changed += {
        event => model.colorLiteral = colorLiteral.getColor
        false
    }

    val colorIdentifier = new ColorPane("vertex.color.identifier",model.colorIdentifier)
    colorIdentifier.changed += {
        event => model.colorIdentifier = colorIdentifier.getColor
        false
    }

    val colorUnknown = new ColorPane("vertex.color.unknown",model.colorUnknown)
    colorUnknown.changed += {
        event => model.colorUnknown = colorUnknown.getColor
        false
    }

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
