package cz.payola.web.client.views.components.bootstrap

import cz.payola.web.client.views._
import cz.payola.web.client.views.elements._

class InputControl(label: String, name: String, value: String, title: String) extends ComposedComponent
{
    val input = new Input(name, value, Some(title))

    private val inputLabel = new Label(label, input)

    private val infoText = new Text("")

    private val infoSpan = new Span(List(infoText), "help-inline")

    private val controls = new Div(List(input, infoSpan), "controls")

    private val controlGroup = new Div(List(inputLabel, controls), "control-group")

    def createSubComponents = List(controlGroup)

    def setError(errorMessage: String) {
        infoText.text = errorMessage
        controlGroup.removeCssClass("success")
        controlGroup.addCssClass("error")
    }

    def setOk() {
        infoText.text = ""
        controlGroup.removeCssClass("error")
        controlGroup.addCssClass("success")
    }
}
