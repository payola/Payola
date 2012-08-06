package cz.payola.web.client.views.graph.visual.settings

import cz.payola.common.visual.Color

class EdgeSettingsModel extends  SettingsModel
{
    private val widthValue = 1

    private val colorValue = new Color(150, 150, 150)

    def width(typeName: String, typePropertyName: String): Int = {
        getProperty(typeName, typePropertyName).map(_.strokeWidth).filter(_ > 0).getOrElse(widthValue)
    }

    def color(typeName: String, typePropertyName: String): Color = {
        getProperty(typeName, typePropertyName).flatMap(c => Color(c.strokeColor)).getOrElse(colorValue)
    }
}
