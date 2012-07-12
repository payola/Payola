package cz.payola.web.client.views.plugins.visual.settings.components.visualsetup

import cz.payola.web.client.views.Component
import cz.payola.web.client.views.plugins.visual.Color
import s2js.adapters.js.dom.Element
import cz.payola.web.client.views.events._
import s2js.compiler.javascript
import cz.payola.web.client.views.elements._

class ColorPane(name: String, label: String, var color: Color) extends Component
{
    val changed = new ChangedEvent[ColorPane]

    val colorInput: Input = new Input(name, color.toString, None, "colorpicker")
    colorInput.field.setAttribute("readonly","true")

    val i = new Italic(List())
    i.i.setAttribute("style","background-color: "+color.toString)

    val span = new Span(List(i),"add-on")
    val labelElement = new Label(label, colorInput.field)
    val div = new Div(List(colorInput, span), "input-append color")
    div.div.setAttribute("data-color", color.toString)
    div.div.setAttribute("data-color-format", "rgba")

    def render(parent: Element) {
        labelElement.render(parent)
        div.render(parent)
        init
    }

    def getColor : Color = {
        color
    }

    def setColor(value: Color) {
        color = value
        colorInput.setText(value.toString)
    }

    def getDomElement : Element = colorInput.getDomElement

    @javascript("""jQuery(self.div.div).colorpicker({format: 'rgba'}).on('changeColor',function(evt){
        var rgba = evt.color.toRGB();
        self.color.red = rgba.r;
        self.color.green = rgba.g;
        self.color.blue = rgba.b;
        self.color.alpha = rgba.a;
    })""")
    private def init = Nil

}
