package cz.payola.web.client.presenters.components

import cz.payola.web.client.mvvm.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.Element
import cz.payola.web.client.mvvm.element._
import cz.payola.web.client.events._

class ZoomControls(var currentZoom: Int) extends Component
{
    val zoomIncreased = new ZoomChangedEvent[ZoomControls]
    val zoomDecreased = new ZoomChangedEvent[ZoomControls]

    private val spanCaption = new Text(getStatusCaption)

    val plus = new Span(List(new Text("+")),"btn btn-primary")
    val status = new Span(List(spanCaption))
    val minus = new Span(List(new Text("-")),"btn btn-primary")

    val wrapper = new Div(List(plus, status, minus), "zoom-controls")

    private def getStatusCaption = {
        currentZoom+" %"
    }

    def setZoom(zoom: Int) = {
        currentZoom = zoom
        spanCaption.setText(getStatusCaption)
    }

    def render(parent: Element = document.body) = {
        wrapper.render(parent)
    }

    plus.clicked += {
        evt =>
            zoomIncreased.trigger(new ZoomChangedEventArgs[ZoomControls](this))
            false
    }

    minus.clicked += {
        evt =>
            zoomDecreased.trigger(new ZoomChangedEventArgs[ZoomControls](this))
            false
    }
}
