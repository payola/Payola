package cz.payola.web.client.views.elements

import s2js.adapters.js.dom
import cz.payola.web.client.views._
import cz.payola.web.client.View

class Form(subViews: Seq[View] = Nil, cssClass: String = "")
    extends ElementView[dom.Element]("form", subViews, cssClass)
