package cz.payola.web.client.views.elements

import s2js.adapters.html
import cz.payola.web.client.View
import cz.payola.web.client.views._

class Paragraph(subViews: Seq[View] = Nil, cssClass: String = "")
    extends ElementView[html.Element]("p", subViews, cssClass)
