package cz.payola.web.client.views.extensions.bootstrap

import cz.payola.web.client.views.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom._
import cz.payola.web.client.views.events._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.events.SimpleEvent
import cz.payola.web.client.views.elements.Input
import cz.payola.web.client.views.elements.Div
import scala.Some

class InputControl(label: String, name: String, value: String, title: String) extends Component
{
    val changed = new SimpleEvent[InputControl]()

    private val input = new Input(name, value, Some(title))

    private val inputLabel = new Label(label, input)

    private val infoText = new Text("")

    private val infoSpan = new Span(List(infoText), "help-inline")

    private val controls = new Div(List(input, infoSpan), "controls")

    private val wrap = new Div(List(inputLabel, controls), "control-group")

    def render(parent: Node) {
        wrap.render(parent)
    }

    def setError(errorMsg: String) {
        infoText.text = errorMsg
        wrap.removeCssClass("success")
        wrap.addCssClass("error")
    }

    def setOk() {
        infoText.text =""
        wrap.removeCssClass("error")
        wrap.addCssClass("success")
    }

    def getValue(): String = {
        input.value
    }

    def setValue(value: String) {
        input.value = value
    }

    input.changed += { args =>
        changed.triggerDirectly(this)
        false
    }

    def domElement: Element = wrap.domElement
}
