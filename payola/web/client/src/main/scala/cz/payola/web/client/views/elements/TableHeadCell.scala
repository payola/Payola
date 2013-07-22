package cz.payola.web.client.views.elements

import s2js.adapters.html
import cz.payola.web.client.views._
import cz.payola.web.client.View

class TableHeadCell(subViews: Seq[View] = Nil, cssClass: String = "")
    extends ElementView[html.Element]("th", subViews, cssClass)
