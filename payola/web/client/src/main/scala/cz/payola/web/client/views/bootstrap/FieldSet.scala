package cz.payola.web.client.views.bootstrap

import cz.payola.web.client.View
import cz.payola.web.client.views.ElementView
import s2js.adapters.js.dom

class FieldSet(innerViews: Seq[View] = Nil, cssClass: String = "")
    extends ElementView[dom.Div]("fieldset", innerViews, cssClass)
{
}
