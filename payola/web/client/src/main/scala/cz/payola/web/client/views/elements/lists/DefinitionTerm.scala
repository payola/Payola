package cz.payola.web.client.views.elements.lists

import s2js.adapters.html
import cz.payola.web.client.views._
import cz.payola.web.client.View

class DefinitionTerm(subViews: Seq[View] = Nil, cssClass: String = "")
    extends ElementView[html.Element]("dt", subViews, cssClass)
