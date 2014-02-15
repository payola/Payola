package cz.payola.web.client.views.map

import cz.payola.common.geo.Coordinates
import cz.payola.web.client.events._
import cz.payola.common.geo.Coordinates

class Marker(coordinates: Coordinates, title: String, description: String){

    val visibilityChanged = new SimpleBooleanEvent[Boolean]
    val colorChanged = new SimpleBooleanEvent[String]

    private var _color = ""

    private var _visibility: Int = 0

    def visibility: Int = _visibility

    def visibility_=(value: Int){
        _visibility = value
        visibilityChanged.trigger(new EventArgs[Boolean](_visibility == 0))
    }

    def isVisible = _visibility == 0

    def setColor(color: String) = {
        _color = color
        colorChanged.trigger(new EventArgs[String](color))
    }

}
