package cz.payola.web.client.views.elements

import s2js.adapters.js.html
import cz.payola.web.client.View
import cz.payola.web.client.views._
import s2js.adapters.html.Element

class Paragraph(subViews: Seq[View] = Nil, cssClass: String = "")
    extends ElementView[html.Element]("p", subViews, cssClass)
