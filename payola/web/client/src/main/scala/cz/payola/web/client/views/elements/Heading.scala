package cz.payola.web.client.views.elements

import s2js.adapters.js.dom
import cz.payola.web.client.views._

class Heading(innerComponents: Seq[Component] = Nil, level: Int = 3, cssClass: String = "")
    extends Element[dom.Element]("h" + level, innerComponents, cssClass)
