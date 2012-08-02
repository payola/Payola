package cz.payola.web.client.views.bootstrap.inputs

import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.InputControl

class TextAreaInputControl(override val label: String, override val name: String, value: String, title: String)
    extends InputControl[TextArea](label, name, value, title, "")
{
    def createInput = new TextArea(name, value, Some(title))
}
