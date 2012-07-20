package cz.payola.web.client.views.graph.visual.settings.components.visualsetup

import cz.payola.web.client.View
import cz.payola.web.client.views.graph.visual.Color
import s2js.adapters.js.dom.Element
import s2js.compiler.javascript
import cz.payola.web.client.views.elements._
import cz.payola.web.client.events._
import cz.payola.web.client.views.bootstrap.Icon

class ColorPane(name: String, label: String, var color: Option[Color]) extends View
{
    val changed = new SimpleUnitEvent[ColorPane]

    val colorInput: Input = new Input(name, if (color.isDefined) color.toString else "No color selected", None, "colorpicker")
    colorInput.setAttribute("readonly", "true")

    val colorWell = new Italic(List())
    colorWell.setAttribute("style", "background-color: " + (if (color.isDefined) { color.get.toString }else{ "white" }))

    val colorWellSpan = new Span(List(colorWell), "add-on")

    val clearIcon = new Icon(Icon.remove)
    val clearColorSpan = new Span(List(clearIcon), "btn")
    clearColorSpan.mouseClicked += { e =>
        this.color = None
        true
    }

    val labelElement = new Label(label, colorInput)

    val div = new Div(List(colorInput, colorWellSpan, clearColorSpan), "input-append color")

    div.setAttribute("data-color", color.toString)
    div.setAttribute("data-color-format", "rgba")

    def render(parent: Element) {
        labelElement.render(parent)
        div.render(parent)
        init
    }

    def getColor: Option[Color] = {
        color
    }

    def getColorHexString = if (color.isDefined) {
        color.get.toHexString
    }else{
        ""
    }

    def setColor(value: Option[Color]) {
        color = value
        colorInput.value = value.toString
    }

    def domElement: Element = colorInput.domElement

    @javascript( """jQuery(self.div.div).colorpicker({format: 'rgba'}).on('changeColor',function(evt){
        var rgba = evt.color.toRGB();
        var color = new Color(rgba.r, rgba.g, rgba.b, rgba.a);
        self.color = Some(color);
    })""")
    private def init = Nil

    def destroy() {
        // TODO
    }

    def blockDomElement: Element = null // TODO
}
