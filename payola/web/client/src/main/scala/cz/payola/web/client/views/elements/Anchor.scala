package cz.payola.web.client.views.elements

import s2js.adapters.html
import cz.payola.web.client.views._
import cz.payola.web.client.View

class Anchor(subViews: Seq[View] = Nil, href: String = "#", cssClass: String = "", title: String = "")
    extends ElementView[html.elements.Anchor]("a", subViews, cssClass)
{
    setAttribute("href", href)
    setAttribute("title", title)
}
