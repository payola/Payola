package cz.payola.web.client.views.bootstrap.inputs

import cz.payola.web.client.views.bootstrap.InputControl
import cz.payola.common.visual.Color
import cz.payola.web.client.views.bootstrap.element.ColorInput

class ColorInputControl(labelString: String, nameString: String, value: String, title: String, cssClass: String = "")
    extends InputControl[ColorInput](labelString, nameString, value, title, cssClass)
{
    def createInput = new ColorInput(name, label, value, cssClass)
}
