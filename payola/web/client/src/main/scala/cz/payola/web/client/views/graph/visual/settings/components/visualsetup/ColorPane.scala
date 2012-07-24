package cz.payola.web.client.views.graph.visual.settings.components.visualsetup

import cz.payola.web.client.View
import cz.payola.web.client.views.graph.visual.Color
import s2js.adapters.js.dom.Element
import s2js.compiler.javascript
import cz.payola.web.client.views.elements._
import cz.payola.web.client.events._
import cz.payola.web.client.views.bootstrap.Icon


// Use the getter + setter for the color
class ColorPane(name: String, label: String, private var colorOption: Option[Color]) extends View
{
    val changed = new SimpleUnitEvent[ColorPane]
    val closed = new SimpleUnitEvent[ColorPane]
    val cleared = new SimpleUnitEvent[ColorPane]

    val colorInput: Input = new Input(name, if (colorOption.isDefined) colorOption.get.toString else "No color selected", None, "colorpicker")
    colorInput.setAttribute("readonly", "true")

    val colorWell = new Italic(List())
    colorWell.setAttribute("style", "background-color: " + (if (colorOption.isDefined) { colorOption.get.toString }else{ "rgba(0, 0, 0, 1)" }))

    val colorWellSpan = new Span(List(colorWell), "add-on")

    val clearIcon = new Icon(Icon.remove)
    val clearColorSpan = new Span(List(clearIcon), "btn")
    clearColorSpan.mouseClicked += { e =>
        setColor(None)
        cleared.trigger(new EventArgs[ColorPane](this))
        true
    }

    val labelElement = new Label(label, colorInput)

    val div = new Div(List(colorInput, colorWellSpan, clearColorSpan), "input-append color")

    div.setAttribute("data-color", if (colorOption.isDefined) { colorOption.get.toString }else{ "rgba(0, 0, 0, 1)" })
    div.setAttribute("data-color-format", "rgba")

    def render(parent: Element) {
        labelElement.render(parent)
        div.render(parent)
        init
        setColor(colorOption)
    }

    def getColor: Option[Color] = {
        colorOption
    }

    def getColorHexString = if (colorOption.isDefined) {
        colorOption.get.toHexString
    }else{
        ""
    }

    def setColor(value: Option[Color]) {
        colorOption = value
        if (value.isDefined) {
            colorInput.value = value.get.toString
        }else{
            colorInput.value = "No color selected"
        }
        changed.trigger(new EventArgs[ColorPane](this))
    }

    def triggerHideEvent() {
        closed.trigger(new EventArgs[ColorPane](this))
    }

    def domElement: Element = colorInput.domElement

    @javascript( """var cp = jQuery(self.div.domElement).colorpicker({format: 'rgba'})
                    cp.on('changeColor',function(evt){
        var rgba = evt.color.toRGB();
        var color = new cz.payola.web.client.views.graph.visual.Color(rgba.r, rgba.g, rgba.b, rgba.a);
        self.setColor(new scala.Some(color));
    });
        cp.on('hide', function(evt) { self.triggerHideEvent(); });
                 """)
    private def init = Nil

    def destroy() {
        // TODO
    }

    def blockDomElement: Element = null // TODO
}
