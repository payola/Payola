package cz.payola.web.client.views.graph.visual.settings

import cz.payola.web.client.views.graph.visual.Color

class VertexSettingsModel extends SettingsModel
{
    var radiusValue = 25

    var colorValue = new Color(51, 204, 255, 0.25)

    var glyphValue: String = ""

    def radius(typeName: String): Int = {
        val foundCustomization = getCustomization(typeName)
        if(foundCustomization.isDefined && foundCustomization.get.radius != 0) {
            foundCustomization.get.radius
        } else {
            radiusValue
        }
    }

    def color(typeName: String): Color = {
        val foundCustomization = getCustomization(typeName)
        if(foundCustomization.isDefined && foundCustomization.get.fillColor.length != 0) {
            val color = Color.fromHex(foundCustomization.get.fillColor)
            color.getOrElse(colorValue)
        } else {
            colorValue
        }
    }

    def glyph(typeName: String): String = {
        val foundCustomization = getCustomization(typeName)
        if(foundCustomization.isDefined && foundCustomization.get.glyph != 0) {
            foundCustomization.get.glyph
        } else {
            glyphValue
        }
    }
}
