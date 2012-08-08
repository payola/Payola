package cz.payola.web.client.views.bootstrap.element

import s2js.adapters.html
import s2js.compiler.javascript
import cz.payola.common.visual.Color
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.web.client.views.elements.form._
import cz.payola.web.client.views.elements.form.fields._
import cz.payola.web.client.views.ComposedView

class ColorInput(name: String, initialValue: Option[Color], cssClass: String = "")
    extends ComposedView with Field[Option[Color]]
{
    private val emptyColorText = "No color selected"

    private val emptyColorValue = "rgb(0, 0, 0)"

    private val colorInput = new TextInput(name, "", "Select color")

    def formHtmlElement = colorInput.htmlElement

    def createSubViews = List(
        colorInput
    )

    def value: Option[Color] = {
        if (colorInput.value == emptyColorText) {
            None
        } else {
            Color(colorInput.value)
        }
    }

    def value_=(newValue: Option[Color]) {
        colorInput.value = newValue.map(_.toString).getOrElse(emptyColorText)
    }

    def isActive = colorInput.isActive

    def isActive_=(newValue: Boolean) {
        colorInput.isActive = newValue
    }

    /*
    colorInput.setAttribute("data-color",  getColorRgbString(Color(initialValue), emptyColorValue))
    colorInput.setAttribute("data-color-format", "rgb")
    colorInput.keyReleased += { e =>
        setColorWellBackgroundColor(getColorRgbString(Color(colorInput.value), emptyColorValue))
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

    def value = {
        val v = colorInput.value
        if (v == emptyColorText){
            ""
        }
        else {
            v
        }
    }

    def value_=(value: String) {
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
        colorInput.value = getColorRgbString(color, emptyColorText)
        setColorWellBackgroundColor(getColorRgbString(color, emptyColorValue))

        changed.triggerDirectly(this)
    }

    private def setColorWellBackgroundColor(rgbColor: String){
        colorWell.setAttribute("style", "background-color: " + rgbColor)
    }

    private def getColorRgbString(color: Option[Color], defaultValue: String): String = {
        color.map(_.toString).getOrElse(defaultValue).toString
    }*/
}
