package cz.payola.web.client.views.elements

import s2js.adapters.js.html

class TextArea(name: String, initialValue: String, title: Option[String], cssClass: String = "")
    extends FormField[html.elements.TextArea]("textarea", name, initialValue, title, cssClass)
