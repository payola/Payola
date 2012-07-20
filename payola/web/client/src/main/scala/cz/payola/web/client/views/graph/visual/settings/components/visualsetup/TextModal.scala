package cz.payola.web.client.views.graph.visual.settings.components.visualsetup

import cz.payola.web.client.views.elements.Div
import cz.payola.web.client.views.graph.visual.settings.TextSettingsModel
import cz.payola.web.client.views.bootstrap.Modal

class TextModal(model: TextSettingsModel) extends Modal("Text settings")
{
    val colorBackground = new ColorPane("text.color.background", "Text background", Some(model.colorBackground))

    colorBackground.changed += { event =>
        model.colorBackground = colorBackground.getColor.get
    }

    val color = new ColorPane("text.color.foreground", "Text foreground", Some(model.color))

    color.changed += { event =>
        model.color = color.getColor.get
    }

    override val body = List(new Div(List(color, colorBackground)))
}
