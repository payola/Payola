package cz.payola.web.client.views.elements

import s2js.adapters.js.dom
import cz.payola.web.client.views._

class Label(text: String, forElement: ElementView[_], cssClass: String = "")
    extends ElementView[dom.Element]("label", Nil, cssClass)
{
    setAttribute("for", forElement.id)
    domElement.innerHTML = text
}
