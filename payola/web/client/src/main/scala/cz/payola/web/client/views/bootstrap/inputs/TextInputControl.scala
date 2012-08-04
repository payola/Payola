package cz.payola.web.client.views.bootstrap.inputs

import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.InputControl

class TextInputControl(override val label: String, override val name: String, value: String, title: String,
    cssClass: String = "")
    extends InputControl[Input](label, name, value, title, cssClass)
{
    def createInput = new Input(name, value, Some(title))
}
