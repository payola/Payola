package cz.payola.web.client.views.elements

class Checkbox(name: String, initialValue: String, title: Option[String], cssClass: String = "",
    inputType: String = "checkbox")
    extends Input(name, initialValue, title, cssClass, inputType)
{
    mouseClicked += {
        e =>
            value = if (value.toBoolean) "false" else "true"
            changed.triggerDirectly(this)
            true
    }

    if (initialValue.toBoolean) {
        htmlElement.setAttribute("checked", "checked")
    }

    override def value_=(value: String) {
        if (value.toBoolean) {
            htmlElement.setAttribute("checked", "checked")
            htmlElement.value = "true"
        } else {
            htmlElement.removeAttribute("checked")
            htmlElement.value = "false"
        }
    }
}
