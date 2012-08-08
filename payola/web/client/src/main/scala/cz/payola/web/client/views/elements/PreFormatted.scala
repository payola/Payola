package cz.payola.web.client.views.elements

import s2js.adapters.html
import cz.payola.web.client.views._

class PreFormatted(content: String, cssClass: String = "")
    extends ElementView[html.Element]("pre", List(new Text(content)), cssClass)
{
    override def render(parent: html.Element) {
        parentElement = Some(parent)
        parent.appendChild(htmlElement)
        subViews.foreach {
            v =>
                v.render(htmlElement)
        }
    }
}
