package cz.payola.web.client.views.elements.lists

import s2js.adapters.html
import cz.payola.web.client.views._
import cz.payola.web.client.View

class ListItem(subViews: Seq[View] = Nil, cssClass: String = "")
    extends ElementView[html.Element]("li", subViews, cssClass)
