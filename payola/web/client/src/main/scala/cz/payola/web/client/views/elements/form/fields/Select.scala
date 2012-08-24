package cz.payola.web.client.views.elements.form.fields

import s2js.adapters.html

class Select(
    name: String,
    initialValue: String,
    title: String = "",
    val options: Seq[SelectOption] = Nil,
    cssClass: String = "")
    extends InputLikeView[html.elements.Select, String]("select", options, name, initialValue, title, cssClass)
{
    def value: String = {
        if (htmlElement.selectedIndex >= 0) {
            options(htmlElement.selectedIndex).htmlElement.value
        } else {
            ""
        }
    }

    def updateValue(newValue: String) {
        options.find(_.htmlElement.value == newValue).foreach(o => htmlElement.selectedIndex = options.indexOf(o))
    }
}
