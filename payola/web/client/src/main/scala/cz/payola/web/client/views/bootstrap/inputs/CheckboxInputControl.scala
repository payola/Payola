package cz.payola.web.client.views.bootstrap.inputs

import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.InputControl

class CheckboxInputControl(labelString: String, nameString: String, value: String, title: String)
    extends InputControl(labelString, nameString, value, title)
{
    def createInput = new Checkbox(name, value, Some(title))
}
