package cz.payola.web.client.views.map

import cz.payola.common.geo.Coordinates
import cz.payola.web.client.events._
import cz.payola.common.geo.Coordinates

class Marker(coordinates: Coordinates, title: String, description: String){

    val visibilityChanged = new SimpleBooleanEvent[Boolean]

    private var _isVisible = true

    def isVisible = _isVisible

    def isVisible_=(value: Boolean) = {
        _isVisible = value
        visibilityChanged.trigger(new EventArgs[Boolean](value))
    }

}
