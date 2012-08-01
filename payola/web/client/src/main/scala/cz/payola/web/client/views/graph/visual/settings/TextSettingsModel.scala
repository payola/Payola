package cz.payola.web.client.views.graph.visual.settings

import cz.payola.web.client.views.graph.visual.Color

class TextSettingsModel extends SettingsModel
{
    var colorBackgroundValue = new Color(255, 255, 255, 0.2)

    var colorValue = new Color(50, 50, 50, 1)

    def colorBackground: Color = colorBackgroundValue

    def color: Color = colorValue
}
