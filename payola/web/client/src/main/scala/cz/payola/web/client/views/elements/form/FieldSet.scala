package cz.payola.web.client.views.elements.form

import s2js.adapters.html
import cz.payola.web.client.View
import cz.payola.web.client.views.ElementView

class FieldSet(subViews: Seq[View] = Nil, cssClass: String = "")
    extends ElementView[html.Element]("fieldset", subViews, cssClass)
