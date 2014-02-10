package cz.payola.web.client.views.elements.lists

import s2js.adapters.html
import cz.payola.web.client.views._
import cz.payola.web.client.View

class UnorderedList(subViews: Seq[View] = Nil, cssClass: String = "list-unstyled")
    extends ElementView[html.Element]("ul", subViews, cssClass)
