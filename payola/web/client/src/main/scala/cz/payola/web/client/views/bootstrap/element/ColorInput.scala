package cz.payola.web.client.views.bootstrap.element

import cz.payola.web.client.views.graph.visual.Color
import s2js.adapters.js.dom
import s2js.compiler.javascript
import cz.payola.web.client.views.elements._
import cz.payola.web.client.events._
import cz.payola.web.client.views.bootstrap.{EditableInput, Icon}

// Use the getter + setter for the color
class ColorInput(name: String, label: String, private var colorOption: Option[Color], cssClass: String = "")
    extends Input(name, colorOption.map(_.toString).getOrElse("").toString, Some("No color selected"), "color")
    with EditableInput
{
    val closed = new SimpleUnitEvent[ColorInput]

    val cleared = new SimpleUnitEvent[ColorInput]

    val colorInput = new Input(name, colorOption.map(_.toString).getOrElse("").toString, Some("No color selected"))

    val colorWell = new Italic(List())

    colorWell.setAttribute("style", "background-color: " + getColorString("rgba(0, 0, 0, 1)") )

    val colorWellSpan = new Span(List(colorWell), "btn")

    val clearIcon = new Icon(Icon.remove)

    val clearColorSpan = new Span(List(clearIcon), "btn")

    clearColorSpan.mouseClicked += {
        e =>
            setColor(None)
            cleared.trigger(new EventArgs[ColorInput](this))
            true
    }

    val labelElement = new Label(label, colorInput)

    val div = new Div(List(colorInput, colorWellSpan, clearColorSpan), "input-append")

    colorInput.setAttribute("data-color",  getColorString("rgba(0, 0, 0, 1)"))
    colorInput.setAttribute("data-color-format", "rgba")

    override def render(parent: dom.Element) {
        labelElement.render(parent)
        div.render(parent)
        init
        setColor(colorOption)
    }

    def getColor: Option[Color] = {
        colorOption
    }

    def getColorHexString = {
        colorOption.map(_.toHexString).getOrElse("")
    }

    private def getColorString(defaultValue: String) = {
        colorOption.map(_.toString).getOrElse(defaultValue).toString
    }

    def setColor(value: Option[Color]) {
        colorOption = value

        colorInput.value = getColorString("No color selected")

        changed.trigger(new EventArgs[ColorInput.this.type](this))
    }

    def triggerHideEvent() {
        closed.trigger(new EventArgs[ColorInput.this.type](this))
    }

    override def value = {
        getColorHexString
    }

    override def value_=(value: String) {
        // During initialization is value set (.ctor), but field doesn't exists yet
        if (colorInput != null){
            setColor(Color.fromHex(value))
        }
    }

    override def setIsActive(isActive: Boolean) {
        colorInput.setIsActive(isActive)
    }

    @javascript("""var cp = jQuery(self.colorInput.domElement).colorpicker({format: 'rgba'})
                    cp.on('changeColor',function(evt){
        var rgba = evt.color.toRGB();
        var color = new cz.payola.web.client.views.graph.visual.Color(rgba.r, rgba.g, rgba.b, rgba.a);
        self.setColor(new scala.Some(color));
    });
        cp.on('hide', function(evt) { self.triggerHideEvent(); });
                 """)
    private def init = Nil
}
