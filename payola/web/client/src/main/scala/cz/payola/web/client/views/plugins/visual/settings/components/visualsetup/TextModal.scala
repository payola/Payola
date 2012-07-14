package cz.payola.web.client.views.plugins.visual.settings.components.visualsetup

import cz.payola.web.client.views.elements.Div
import cz.payola.web.client.views.plugins.visual.settings.TextSettingsModel
import cz.payola.web.client.views.components.bootstrap.Modal

class TextModal(model: TextSettingsModel) extends Modal("Text settings")
{
    val colorBackground = new ColorPane("text.color.background", "Text background", model.colorBackground)
    colorBackground.changed += { event =>
        model.colorBackground = colorBackground.getColor
    }

    val color = new ColorPane("text.color.foreground", "Text foreground", model.color)
    color.changed += { event =>
        model.color = color.getColor
    }

    override val body = List(new Div(List(color, colorBackground)))
}
