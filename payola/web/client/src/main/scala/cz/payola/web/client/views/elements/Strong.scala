package cz.payola.web.client.views.elements

import s2js.adapters.js.dom
import cz.payola.web.client.views._

class Strong(innerComponents: Seq[Component] = Nil, cssClass: String = "")
    extends Element[dom.Div]("strong", innerComponents, cssClass)
