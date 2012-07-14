package cz.payola.web.client.views.elements

import s2js.adapters.js.dom
import cz.payola.web.client.views._

class Button(text: String, cssClass: String = "")
    extends Element[dom.Button]("button", List(new Text(text)), cssClass)
{
    setAttribute("type", "button")
}
