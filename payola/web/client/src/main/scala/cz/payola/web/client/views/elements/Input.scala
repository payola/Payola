package cz.payola.web.client.views.elements

import s2js.adapters.js.dom

class Input(name: String, initialValue: String, title: Option[String], cssClass: String = "", inputType: String = "text")
    extends FormField[dom.Input]("input", name, initialValue, title, cssClass)
{
    setAttribute("type", inputType)
}
