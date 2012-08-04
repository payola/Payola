package cz.payola.web.client.views.elements

import s2js.adapters.js.html
import cz.payola.web.client.views._
import s2js.adapters.html.Element
import s2js.adapters

class PreFormatted(content: String, cssClass: String = "")
    extends ElementView[html.Element]("pre", List(new Text(content)), cssClass)
{
    override def render(parent: adapters.html.Element) {
        parentElement = Some(parent)
        parent.appendChild(htmlElement)
        subViews.foreach {
            v =>
                v.render(htmlElement)
        }
    }
}
