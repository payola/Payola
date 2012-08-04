package cz.payola.web.client.views.elements

import s2js.adapters.js.html
import cz.payola.web.client.views._
import cz.payola.web.client.View
import s2js.adapters.html.Element

class Heading(subViews: Seq[View] = Nil, level: Int = 3, cssClass: String = "")
    extends ElementView[html.Element]("h" + level, subViews, cssClass)
