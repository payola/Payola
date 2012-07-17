package cz.payola.web.client.views.elements

import cz.payola.web.client.View
import cz.payola.web.client.views.ElementView
import s2js.adapters.js.dom

class SelectOption(innerViews: Seq[View] = Nil, cssClass: String = "")
    extends ElementView[dom.Element]("option", innerViews, cssClass)
{
}
