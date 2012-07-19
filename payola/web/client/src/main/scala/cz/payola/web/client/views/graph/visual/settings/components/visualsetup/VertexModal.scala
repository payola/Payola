package cz.payola.web.client.views.graph.visual.settings.components.visualsetup

import cz.payola.web.client.views.elements.{Label, Input, Div}
import cz.payola.web.client.views.bootstrap.Modal
import cz.payola.web.client.views.graph.visual.settings.VertexSettingsModel

class VertexModal(model: VertexSettingsModel) extends Modal("Vertex settings")
{
    //TODO add some bounds check for every input
    val radius = new Input("vertex.radius", model.radius.toString(), Some("0 < x < 100"))

    val rLabel = new Label("Corner radius [px]:", radius)

    radius.changed += { event =>
        model.radius = radius.value.toInt
        false
    }

    val color = new ColorPane("vertex.color", "Vertex color", Some(model.color))

    color.changed += { event =>
        model.color = color.getColor.get
    }

    val colorSelected = new ColorPane("vertex.color.selected", "Vertex color (selected)", Some(model.colorSelected))

    colorSelected.changed += { event =>
        model.colorSelected = colorSelected.getColor.get
    }

    override val body = List(new Div(List(rLabel, radius, color, colorSelected)))
}
