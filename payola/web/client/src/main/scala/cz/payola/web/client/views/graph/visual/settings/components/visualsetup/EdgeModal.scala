package cz.payola.web.client.views.graph.visual.settings.components.visualsetup

import cz.payola.web.client.views.graph.visual.settings.EdgeSettingsModel
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Modal
import cz.payola.web.client.views.elements.Input
import cz.payola.web.client.views.elements.Div

class EdgeModal(model: EdgeSettingsModel) extends Modal("Edge settings")
{
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

    val colorSelect = new ColorPane("edge.color.select", "Edge color (selected)", Some(model.colorSelected))

    colorSelect.changed += { event =>
        model.colorSelected = colorSelect.getColor.get
        false
    }

    val color = new ColorPane("edge.color.base", "Edge color", Some(model.color))

    color.changed += { event =>
        model.color = color.getColor.get
        false
    }

    override val body = List(new Div(List(wLabel, width, sLabel, straightenIndex, colorSelect, color)))
}
