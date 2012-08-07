package cz.payola.web.client.views.graph.visual.settings

import cz.payola.common.visual.Color

class TextSettingsModel extends SettingsModel
{
    private val colorBackgroundValue = new Color(255, 255, 255)

    private val colorValue = new Color(50, 50, 50)

    var fontValue = "12px Sans"

    var alignValue  = "center"

    def colorBackground: Color = colorBackgroundValue

    def color: Color = colorValue

    def font: String = fontValue

    def align: String = alignValue
}
