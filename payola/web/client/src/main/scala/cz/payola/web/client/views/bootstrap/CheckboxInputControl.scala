package cz.payola.web.client.views.bootstrap

import cz.payola.web.client.views.elements._

class CheckboxInputControl(override val label: String, override val name: String, value: String, title: String)
    extends InputControl(label, name, value, title)
{
    def createInput = new Checkbox(name, value, Some(title))
}
