package cz.payola.web.client.views.elements

import s2js.adapters.js.html
import cz.payola.web.client.views._
import cz.payola.web.client.View
import s2js.adapters.html.Element

class UnorderedList(subViews: Seq[View] = Nil, cssClass: String = "")
    extends ElementView[html.Element]("ul", subViews, cssClass)
