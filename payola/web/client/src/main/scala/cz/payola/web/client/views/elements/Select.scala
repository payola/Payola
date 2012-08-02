package cz.payola.web.client.views.elements

import s2js.adapters.js.html
import cz.payola.web.client.views.ElementView
import cz.payola.web.client.views.bootstrap.EditableInput

class Select(val options: Seq[SelectOption] = Nil, cssClass: String = "")
    extends ElementView[html.elements.Select]("select", options, cssClass) with EditableInput
{
    htmlElement.onchange = { _ =>
        changed.triggerDirectly(this)
        true
    }

    def value: String = options(htmlElement.selectedIndex).htmlElement.value

    def value_=(newValue: String) {
        options.find(_.htmlElement.value == newValue).foreach(o => htmlElement.selectedIndex = options.indexOf(o))
    }

    def setIsActive(isActive: Boolean = true) {
        // TODO
    }
}
