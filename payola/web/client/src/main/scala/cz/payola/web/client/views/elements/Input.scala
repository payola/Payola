package cz.payola.web.client.views.elements

import s2js.adapters.js.html

class Input(name: String, initialValue: String, title: Option[String], cssClass: String = "", inputType: String = "text")
    extends FormField[html.elements.Input]("input", name, initialValue, title, cssClass)
{
    setAttribute("type", inputType)
}
