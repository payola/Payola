package cz.payola.web.client.views.elements

import s2js.adapters.js.html
import cz.payola.web.client.View
import cz.payola.web.client.views.ElementView
import s2js.adapters.html.Element

class FieldSet(subViews: Seq[View] = Nil, cssClass: String = "")
    extends ElementView[html.Element]("fieldset", subViews, cssClass)
