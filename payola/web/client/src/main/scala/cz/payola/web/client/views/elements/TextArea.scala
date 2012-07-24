package cz.payola.web.client.views.elements

import s2js.adapters.js.dom

class TextArea(name: String, initialValue: String, title: Option[String], cssClass: String = "")
    extends FormField[dom.TextArea]("textarea", name, initialValue, title, cssClass)
{

}
