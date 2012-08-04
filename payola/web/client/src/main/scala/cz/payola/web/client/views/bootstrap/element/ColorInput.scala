package cz.payola.web.client.views.bootstrap.element

import cz.payola.web.client.views.graph.visual.Color
import s2js.adapters.html
import s2js.compiler.javascript
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.{EditableInput, Icon}

// Use the getter + setter for the color
class ColorInput(name: String, label: String, private var colorOption: Option[Color], cssClass: String = "")
    extends Input(name, colorOption.map(_.toString).getOrElse("").toString, Some("No color selected"), cssClass)
    with EditableInput
{
    val colorInput = new Input(name, colorOption.map(_.toString).getOrElse("").toString, Some(NO_COLOR_TEXT))

    private val colorWell = new Italic(List())

    //colorWell.setAttribute("style", "background-color: " + getColorRgbaString("rgba(0, 0, 0, 1)") )

    private val colorWellSpan = new Span(List(colorWell), "add-on")

    private val clearIcon = new Icon(Icon.remove)

    private val clearColorSpan = new Span(List(clearIcon), "btn")

    private val labelElement = new Label(label, colorInput)

    private val div = new Div(List(colorInput, colorWellSpan, clearColorSpan), "input-append color")

    private val NO_COLOR_TEXT = "No color selected"

    private val NO_COLOR_RGBA_VALUE = "rgba(0, 0, 0, 1)"

    colorInput.setAttribute("data-color", getColorRgbaString(NO_COLOR_RGBA_VALUE))
    colorInput.setAttribute("data-color-format", "rgba")

    clearColorSpan.mouseClicked += { e =>
        setColor(None)
        //cleared.trigger(new EventArgs[ColorInput](this))
        true
    }

    override def render(parent: html.Element) {
        labelElement.render(parent)
        div.render(parent)
        init
        setColor(colorOption)
    }

    def getColor: Option[Color] = {
        colorOption
    }

    override def value = {
        getColorHexString
    }

    override def value_=(value: String) {
        // During initialization is value set (.ctor), but field doesn't exists yet
        if (colorInput != null) {
            setColor(Color.fromHex(value))
        }
    }

    override def setIsActive(isActive: Boolean) {
        colorInput.setIsActive(isActive)
    }

    @javascript( """var cp = jQuery(self.colorInput.htmlElement).colorpicker({format: 'rgba'})
                    cp.on('changeColor',function(evt){
        var rgba = evt.color.toRGB();
        var color = new cz.payola.web.client.views.graph.visual.Color(rgba.r, rgba.g, rgba.b, rgba.a);
        self.setColor(new scala.Some(color));
    });
                 """)
    private def init = Nil

    private def getColorHexString = {
        colorOption.map(_.toHexString).getOrElse("")
    }

    private def getColorRgbaString(defaultValue: String) = {
        colorOption.map(_.toString).getOrElse(defaultValue).toString
    }

    private def setColor(color: Option[Color]) {
        colorOption = color

        colorInput.value = getColorRgbaString(NO_COLOR_TEXT)
        colorWell.setAttribute("style", "background-color: " + getColorRgbaString(NO_COLOR_RGBA_VALUE))

        changed.triggerDirectly(this)
    }
}
