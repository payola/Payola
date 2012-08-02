package cz.payola.web.client.views.graph.visual.settings.components.visualsetup

import cz.payola.web.client.views.elements.{Label, Input, Div}
import cz.payola.web.client.views.bootstrap.Modal
import cz.payola.web.client.views.graph.visual.settings.VertexSettingsModel
import cz.payola.web.client.views.bootstrap.element.ColorInput

class VertexModal(model: VertexSettingsModel) extends Modal("Vertex settings")
{
    //TODO add some bounds check for every input
    val radius = new Input("vertex.radius", model.radiusValue.toString(), Some("0 < x < 100"))

    val rLabel = new Label("Corner radius [px]:", radius)

    radius.changed += { event =>
        model.radiusValue = radius.value.toInt
        false
    }

    val color = new ColorInput("vertex.color", "Vertex color", Some(model.colorValue))

    color.changed += { event =>
        model.colorValue = color.getColor.get
    }

    override val body = List(new Div(List(rLabel, radius, color)))
}
