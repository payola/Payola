package cz.payola.web.client.views.bootstrap.inputs

import cz.payola.web.client.views.bootstrap.InputControl
import cz.payola.web.client.views.elements.FileInput

class FileInputControl(labelString: String, nameString: String, title: String, cssClass: String = "")
    extends InputControl[FileInput](labelString, nameString, "", title, cssClass)
{
    def createInput = new FileInput(name, Some(title))
}
