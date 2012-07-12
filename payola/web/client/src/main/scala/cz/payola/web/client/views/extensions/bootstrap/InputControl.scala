package cz.payola.web.client.views.extensions.bootstrap

import cz.payola.web.client.views.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.Element
import cz.payola.web.client.views.events._
import cz.payola.web.client.views.elements._

class InputControl(label: String, name: String, value: String, title: String) extends Component
{
    val changed = new ChangedEvent[InputControl]()

    private val input = new Input(name, value, Some(title))

    private val inputLabel = new Label(label, input.getDomElement)

    private val infoText = new Text("")

    private val infoSpan = new Span(List(infoText), "help-inline")

    private val controls = new Div(List(input, infoSpan), "controls")

    private val wrap = new Div(List(inputLabel, controls), "control-group")

    def render(parent: Element = document.body) {
        wrap.render(parent)
    }

    def setError(errorMsg: String) {
        infoText.setText(errorMsg)
        wrap.removeClass("success")
        wrap.addClass("error")
    }

    def setOk() {
        infoText.setText("")
        wrap.removeClass("error")
        wrap.addClass("success")
    }

    def getValue(): String = {
        input.getText
    }

    def setValue(value: String) {
        input.setText(value)
    }

    input.changed += { args =>
        changed.trigger(new ChangedEventArgs[InputControl](this))
    }

    def getDomElement: Element = wrap.getDomElement
}
