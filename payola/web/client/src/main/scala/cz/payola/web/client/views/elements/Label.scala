package cz.payola.web.client.views.elements

import s2js.adapters.js.html
import cz.payola.web.client.views._

class Label(text: String, forElement: ElementView[_], cssClass: String = "")
    extends ElementView[html.Element]("label", Nil, cssClass)
{
    setAttribute("for", forElement.id)
    htmlElement.innerHTML = text
}
