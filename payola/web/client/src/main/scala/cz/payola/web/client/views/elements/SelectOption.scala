package cz.payola.web.client.views.elements

import cz.payola.web.client.View
import cz.payola.web.client.views.ElementView
import s2js.adapters.js.html

class SelectOption(subViews: Seq[View] = Nil, cssClass: String = "")
    extends ElementView[html.elements.Option]("option", subViews, cssClass)
