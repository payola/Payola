package cz.payola.web.client.views.elements

import s2js.adapters.js.dom
import cz.payola.web.client.views._
import cz.payola.web.client.View

class Anchor(subViews: Seq[View] = Nil, href: String = "#", cssClass: String = "")
    extends ElementView[dom.Anchor]("a", subViews, cssClass)
{
    setAttribute("href", href)
}
