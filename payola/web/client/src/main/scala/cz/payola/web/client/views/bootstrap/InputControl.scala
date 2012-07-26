package cz.payola.web.client.views.bootstrap

import s2js.adapters.js.dom
import s2js.adapters.js.browser.window
import cz.payola.web.client.views._
import cz.payola.web.client.views.elements._
import cz.payola.common.exception.ValidationException
import cz.payola.web.client.events.SimpleUnitEvent

abstract class InputControl(
    val label: String,
    val name: String,
    value: String,
    title: String,
    cssClass: String = "")
    extends ComposedView
{
    val delayedChanged = new SimpleUnitEvent[this.type]

    val input = createInput

    private val inputLabel = new Label(label, input)

    private val infoText = new Text("")

    private val infoSpan = new Span(List(infoText), "help-inline")

    private val controls = new Div(List(input, infoSpan), "controls")

    private val controlGroup = new Div(List(inputLabel, controls), "control-group " + cssClass)

    private var delayedChangedTimeout: Option[Int] = None

    input.changed += { e =>
        delayedChangedTimeout.foreach(window.clearTimeout(_))
        delayedChangedTimeout = Some(window.setTimeout({ () =>
            delayedChanged.triggerDirectly(this)
        }, 1000))
        true
    }

    def createSubViews = List(controlGroup)

    def createInput: FormField[_ <: dom.Input]

    def setState(exception: ValidationException, fieldName: String) {
        if (fieldName == exception.fieldName) {
            setError(exception.message)
        } else {
            setOk()
        }
    }

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

    def setIsActive(isActive: Boolean = true) {
        input.setIsActive(isActive)
    }
}
