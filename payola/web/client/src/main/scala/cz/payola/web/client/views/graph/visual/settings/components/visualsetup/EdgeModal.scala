package cz.payola.web.client.views.graph.visual.settings.components.visualsetup

import cz.payola.web.client.views.graph.visual.settings.EdgeSettingsModel
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Modal
import cz.payola.web.client.views.elements.Input
import cz.payola.web.client.views.elements.Div
import cz.payola.web.client.views.bootstrap.element.ColorInput

class EdgeModal(model: EdgeSettingsModel) extends Modal("Edge settings")
{
    val width = new Input("edge.width", model.widthValue.toString(), None)

    val wLabel = new Label("Width [px]:", width)

    width.changed += { event =>
        model.widthValue = width.value.toInt
        false
    }

    val color = new ColorInput("edge.color.base", "Edge color", Some(model.colorValue))

    color.changed += { event =>
        model.colorValue = color.getColor.get
        false
    }

    override val body = List(new Div(List(wLabel, width, color)))
}
