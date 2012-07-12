package cz.payola.web.client.views.extensions.bootstrap

import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.elements._

class Button(caption: String, style: String = "") extends Span(List(new Text(caption)), "btn " + style)
{
}
