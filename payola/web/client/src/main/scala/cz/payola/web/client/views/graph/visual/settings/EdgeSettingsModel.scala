package cz.payola.web.client.views.graph.visual.settings

import cz.payola.web.client.views.graph.visual.Color

class EdgeSettingsModel extends SettingsModel
{
    var widthValue = 1

    var colorValue = new Color(150, 150, 150, 0.4)

    def width(typeName: String, typePropertyName: String): Int = {
        val foundProperty = getProperty(typeName, typePropertyName)
        if (foundProperty.isDefined && foundProperty.get.strokeWidth != 0) {
            foundProperty.get.strokeWidth
        } else {
            widthValue
        }
    }

    def color(typeName: String, typePropertyName: String): Color = {
        val foundProperty = getProperty(typeName, typePropertyName)

        if (foundProperty.isDefined && foundProperty.get.strokeColor.length != 0) {
            Color.fromHex(foundProperty.get.strokeColor).getOrElse(colorValue)
        } else {
            colorValue
        }
    }
}
