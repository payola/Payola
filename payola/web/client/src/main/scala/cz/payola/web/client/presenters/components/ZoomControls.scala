package cz.payola.web.client.presenters.components

import s2js.adapters.html
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.web.client.views.elements._
import cz.payola.web.client.events.SimpleUnitEvent
import cz.payola.web.client.views.elements.Div
import cz.payola.web.client.views.ComposedView
import s2js.adapters.html.Element

class ZoomControls(var currentZoom: Double) extends ComposedView
{
    val zoomIncreased = new SimpleUnitEvent[ZoomControls]

    val zoomDecreased = new SimpleUnitEvent[ZoomControls]

    /** How much zoom (movement) causes one rotation of mouse wheel. */
    val zoomStep = 0.15

    private val zoomOrigin = currentZoom

    private val maximumZoomOut = 10

    private val maximumZoomIn = 500

    private val plus = new Button(new Icon(Icon.zoom_in))

    private val minus = new Button(new Icon(Icon.zoom_out))

    private val currentZoomText = new Text("")

    private val currentZoomSpan = new Span(List(currentZoomText))

    plus.mouseClicked += { e =>
        zoomIncreased.triggerDirectly(this)
        false
    }

    minus.mouseClicked += { e =>
        zoomDecreased.triggerDirectly(this)
        false
    }

    def createSubViews = {
        List(new Div(List(minus, currentZoomSpan, plus), "pull-right").setAttribute("style", "margin: 0 5px;"))
    }

    def reset() {
        setZoom(zoomOrigin)
    }

    def canZoomIn: Boolean = {
        currentZoom < maximumZoomIn
    }

    def canZoomOut: Boolean = {
        currentZoom > maximumZoomOut
    }

    private def getStatusCaption = {
        val wholeNum = math.round(currentZoom)

        "%3d %%".format(wholeNum)
    }

    def setZoom(zoom: Double) {
        currentZoom = zoom
        currentZoomText.text = getStatusCaption
    }

    def increaseZoomInfo() {
        setZoom(currentZoom + (zoomStep*50))
    }

    def decreaseZoomInfo() {
        setZoom(currentZoom - (zoomStep*50))
    }

    override def render(parent: html.Element) {
        super.render(parent)
        currentZoomText.text = getStatusCaption
    }
}
