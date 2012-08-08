package cz.payola.web.client.views.elements.form

import s2js.adapters.html
import cz.payola.web.client.views._

class Label(text: String, forElement: html.Element, cssClass: String = "")
    extends ElementView[html.Element]("label", Nil, cssClass)
{
    setAttribute("for", forElement.id)
    htmlElement.innerHTML = text
}
