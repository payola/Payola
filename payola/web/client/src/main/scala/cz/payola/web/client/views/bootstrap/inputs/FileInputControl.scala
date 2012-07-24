package cz.payola.web.client.views.bootstrap.inputs

import cz.payola.web.client.views.bootstrap.InputControl
import cz.payola.web.client.views.elements.FileInput

class FileInputControl(labelString: String, nameString: String, title: String)
    extends InputControl(labelString, nameString, "", title)
{
    def createInput = new FileInput(name, Some(title))
}
