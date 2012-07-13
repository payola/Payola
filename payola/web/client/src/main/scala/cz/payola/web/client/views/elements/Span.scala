package cz.payola.web.client.views.elements

import s2js.adapters.js.dom
import cz.payola.web.client.views._

class Span(innerComponents: Seq[Component] = Nil, cssClass: String = "")
    extends Element[dom.Element]("span", innerComponents, cssClass)
