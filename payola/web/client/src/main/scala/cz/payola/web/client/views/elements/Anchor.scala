package cz.payola.web.client.views.elements

import s2js.adapters.js.dom
import cz.payola.web.client.views._

class Anchor(innerComponents: Seq[Component] = Nil, href: String = "#", cssClass: String = "")
    extends Element[dom.Anchor]("a", innerComponents, cssClass)
{
    setAttribute("href", href)
}
