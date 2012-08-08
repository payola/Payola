package cz.payola.web.client.views.elements.form

import s2js.adapters.html
import cz.payola.web.client.views._
import cz.payola.web.client.View

class Form(subViews: Seq[View] = Nil, cssClass: String = "")
    extends ElementView[html.Element]("form", subViews, cssClass)
