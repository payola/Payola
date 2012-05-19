package cz.payola.web.client.views.plugins.visual.settings.components.visualsetup

import cz.payola.web.client.mvvm_api.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.Element
import cz.payola.web.client.events.{EventArgs, ComponentEvent}
import cz.payola.web.client.views.plugins.visual.settings.VertexSettingsModel
import cz.payola.web.client.mvvm_api.element.{Label, Input, Div}

class VertexModal(model: VertexSettingsModel) extends Component
{
    val settingsChanged = new ComponentEvent[VertexModal, EventArgs[VertexModal]]

    //TODO add some bounds check for every input
    val cornerRadius = new Input("vertex.corner.radius", model.cornerRadius.toString(), Some("0 ≤ x ≤ 15"))
    val crLabel = new Label("Corner radius [px]:", cornerRadius.field)
    cornerRadius.changed += { event =>
        model.cornerRadius = cornerRadius.getText.toInt
        false
    }

    val width = new Input("vertex.width", model.width.toString(), Some("20 ≤ x ≤ 50"))
    val wLabel = new Label("Width [px]:", width.field)
    width.changed += { event =>
        model.width = width.getText.toInt
        false
    }

    val height = new Input("vertex.height", model.height.toString(), Some("20 ≤ x ≤ 50"))
    val hLabel = new Label("Height [px]:", height.field)
    height.changed += { event =>
        model.height = height.getText.toInt
        false
    }

    val colorLow = new ColorPane("vertex.color.low","Vertex color (low)",model.colorLow)
    colorLow.changed += { event =>
        model.colorLow = colorLow.getColor
        false
    }

    val colorMed = new ColorPane("vertex.color.medium","Vertex color (medium)",model.colorMed)
    colorMed.changed += { event =>
        model.colorMed = colorMed.getColor
        false
    }

    val colorHigh = new ColorPane("vertex.color.high","Vertex color (selected):", model.colorHigh)
    colorHigh.changed += { event =>
        model.colorHigh = colorHigh.getColor
        false
    }

    val colorLiteral = new ColorPane("vertex.color.literal","Vertex color (literal):",model.colorLiteral)
    colorLiteral.changed += { event =>
        model.colorLiteral = colorLiteral.getColor
        false
    }

    val colorIdentifier = new ColorPane("vertex.color.identifier","Vertex color (ID):",model.colorIdentifier)
    colorIdentifier.changed += { event =>
        model.colorIdentifier = colorIdentifier.getColor
        false
    }

    val colorUnknown = new ColorPane("vertex.color.unknown","Vertex color (unknown):",model.colorUnknown)
    colorUnknown.changed += { event =>
        model.colorUnknown = colorUnknown.getColor
        false
    }

    val literalIcon = new Input("vertex.icon.literal", model.literalIcon, None)
    val liLabel = new Label("Literal icon:", literalIcon.field)

    val identifierIcon = new Input("vertex.icon.identifier", model.identifierIcon, None)
    val idLabel = new Label("ID icon:", identifierIcon.field)

    val unknownIcon = new Input("vertex.icon.unknow", model.unknownIcon, None)
    val unknownLabel = new Label("Unknown icon:", unknownIcon.field)

    val wrapper = new Div(
        List(crLabel, cornerRadius, wLabel, width, hLabel, height,
            colorLow, colorMed, colorHigh, colorLiteral, colorIdentifier, colorUnknown,
            liLabel, literalIcon, idLabel, identifierIcon, unknownLabel, unknownIcon))

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
}