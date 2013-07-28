package cz.payola.web.client.views.elements

import s2js.adapters.html
import cz.payola.web.client.views._
import cz.payola.web.client.View

/**
 *
 * @param subViews
 * @param cssClass
 * @author Jiri Helmich
 */
class TableHead(subViews: Seq[View] = Nil, cssClass: String = "")
    extends ElementView[html.Element]("thead", subViews, cssClass)
