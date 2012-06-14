package cz.payola.web.client.mvvm.element.extensions.Bootstrap

import cz.payola.web.client.mvvm.element._

class Button(caption: String, style : String = "") extends Span(List(new Text(caption)), "btn "+style)
{
}
