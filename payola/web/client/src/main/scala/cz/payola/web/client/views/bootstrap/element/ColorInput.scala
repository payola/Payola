package cz.payola.web.client.views.bootstrap.element

import s2js.adapters.html
import s2js.compiler.javascript
import cz.payola.common.visual.Color
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.{EditableInput, Icon}

class ColorInput(name: String, label: String, initialValue: String, cssClass: String = "")
    extends Input(name, "", Some("Select color"), cssClass) with EditableInput
{
    private val NO_COLOR_TEXT = "No color selected"

    private val NO_COLOR_RGB_VALUE = "rgb(0, 0, 0)"

    private val colorInput = new Input(name, initialValue, Some("Select color"))
    colorInput.value = getColorRgbString(Color(initialValue), NO_COLOR_TEXT)
    colorInput.setAttribute("data-color",  getColorRgbString(Color(initialValue), NO_COLOR_RGB_VALUE))
    colorInput.setAttribute("data-color-format", "rgb")
    colorInput.keyReleased += { e =>
        setColorWellBackgroundColor(getColorRgbString(Color(colorInput.value), NO_COLOR_RGB_VALUE))
        changed.triggerDirectly(this)

        true
    }

    private val colorWell = new Italic(List())

    private val colorWellSpan = new Span(List(colorWell), "add-on")

    private val clearIcon = new Icon(Icon.remove)

    private val clearColorSpan = new Span(List(clearIcon), "btn")

    clearColorSpan.mouseClicked += { e =>
        setColor(None)
        true
    }

    private val labelElement = new Label(label, colorInput)

    private val div = new Div(List(colorInput, colorWellSpan, clearColorSpan), "input-append color")

    override def render(parent: html.Element) {
        labelElement.render(parent)
        div.render(parent)
        init
    }

    override def value = {
        val v = colorInput.value
        if (v == NO_COLOR_TEXT){
            ""
        }
        else {
            v
        }
    }

    override def value_=(value: String) {
        // During initialization is value set (.ctor), but field doesn't exists yet
        if (colorInput != null){
            setColor(Color(value))
        }
    }

    override def setIsActive(isActive: Boolean) {
        colorInput.setIsActive(isActive)
    }

    @javascript("""var cp = jQuery(self.colorInput.domElement).colorpicker({format: 'rgb'})
                    cp.on('changeColor',function(evt){
        var rgb = evt.color.toRGB();
        self.setColor(new scala.Some(new cz.payola.common.visual.Color(rgb.r, rgb.g, rgb.b)));
    });
                 """)
    private def init = Nil

    private def setColor(color: Option[Color]) {
        colorInput.value = getColorRgbString(color, NO_COLOR_TEXT)
        setColorWellBackgroundColor(getColorRgbString(color, NO_COLOR_RGB_VALUE))

        changed.triggerDirectly(this)
    }

    private def setColorWellBackgroundColor(rgbColor: String){
        colorWell.setAttribute("style", "background-color: " + rgbColor)
    }

    private def getColorRgbString(color: Option[Color], defaultValue: String): String = {
        color.map(_.toString).getOrElse(defaultValue).toString
    }
}
