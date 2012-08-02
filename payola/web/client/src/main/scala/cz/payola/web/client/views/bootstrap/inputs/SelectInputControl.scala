package cz.payola.web.client.views.bootstrap.inputs

import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.InputControl

class SelectInputControl(
    options: Seq[SelectOption],
    labelString: String,
    nameString: String,
    value: String,
    title: String)
    extends InputControl[Select](labelString, nameString, value, title)
{
    def createInput = new Select(options)
}
