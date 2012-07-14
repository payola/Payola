package cz.payola.web.client.presenters.components

import cz.payola.web.client.views.Component
import s2js.adapters.js.dom
import cz.payola.web.client.views.components.bootstrap.Icon
import cz.payola.web.client.views.elements._
import cz.payola.web.client.events.SimpleUnitEvent
import cz.payola.web.client.views.elements.Div

class ZoomControls(var currentZoom: Double) extends Component
{
    /**
      * How much zoom (movement) causes one rotation of mouse wheel.
      */
    val zoomStep = 0.09

    private val zoomOrigin = currentZoom

    private val maximumZoomOut = 25
    private val maximumZoomIn = 200

    val zoomIncreased = new SimpleUnitEvent[ZoomControls]
    val zoomDecreased = new SimpleUnitEvent[ZoomControls]

    private val spanCaption = new Text("")

    val plus = new Span(List(new Icon(Icon.zoom_in)),"btn")
    val status = new Span(List(spanCaption))
    val minus = new Span(List(new Icon(Icon.zoom_out)),"btn")

    val wrapper = new Div(List(plus, status, minus), "zoom-controls")

    var parentElement: Option[dom.Element] = None

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

        if(10 < wholeNum && wholeNum < 100) {
            " " + wholeNum + " %"
        } else {
            wholeNum + " %"
        }
    }

    def setZoom(zoom: Double) {
        currentZoom = zoom
        spanCaption.text = (getStatusCaption)
    }

    def increaseZoomInfo() {
        setZoom(currentZoom + (zoomStep*50))
    }

    def decreaseZoomInfo() {
        setZoom(currentZoom - (zoomStep*50))
    }

    override def destroy() {
        //spanCaption.setText("")

        if(parentElement.isDefined) {
            wrapper.domElement.removeChild(minus.domElement)
            wrapper.domElement.removeChild(status.domElement)
            wrapper.domElement.removeChild(plus.domElement)
            parentElement.get.removeChild(wrapper.domElement)
        }
    }

    def render(parent: dom.Element) {
        wrapper.render(parent)
        spanCaption.text = getStatusCaption
        parentElement = Some(parent)

        plus.mouseClicked += { e =>
            zoomIncreased.triggerDirectly(this)
            false
        }

        minus.mouseClicked += { e =>
            zoomDecreased.triggerDirectly(this)
            false
        }
    }

    def domElement : dom.Element = {
        wrapper.domElement
    }
}
