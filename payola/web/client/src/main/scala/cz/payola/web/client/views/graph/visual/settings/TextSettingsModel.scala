package cz.payola.web.client.views.graph.visual.settings

import cz.payola.web.client.views.graph.visual.Color

class TextSettingsModel extends SettingsModel
{
    var colorBackgroundValue = new Color(255, 255, 255, 0.2)

    var colorValue = new Color(50, 50, 50, 1)

    var fontValue = "12px Sans"

    var alignValue  = "center"

    def colorBackground: Color = colorBackgroundValue

    def color: Color = colorValue

    def font: String = fontValue

    def align: String = alignValue
}
