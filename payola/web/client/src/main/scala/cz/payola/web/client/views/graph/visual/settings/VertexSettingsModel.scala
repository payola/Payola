package cz.payola.web.client.views.graph.visual.settings

import cz.payola.common.visual.Color

class VertexSettingsModel extends SettingsModel
{
    private val radiusValue = 25

    private val colorValue = new Color(51, 204, 255)

    private val glyphValue: String = ""

    def radius(typeName: String): Int = {
        getCustomization(typeName).map(_.radius).filter(_ > 0).getOrElse(radiusValue)
    }

    def color(typeName: String): Color = {
        getCustomization(typeName).flatMap(c => Color(c.fillColor)).getOrElse(colorValue)
    }

    def glyph(typeName: String): String = {
        getCustomization(typeName).map(_.glyph).filter(_.length > 0).getOrElse(glyphValue)
    }
}
