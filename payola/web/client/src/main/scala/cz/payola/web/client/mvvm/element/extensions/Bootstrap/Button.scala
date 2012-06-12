package cz.payola.web.client.mvvm.element.extensions.Bootstrap

import cz.payola.web.client.mvvm.element._
import s2js.adapters.js.dom.Element
import s2js.adapters.js.browser.document
import cz.payola.web.client.events.ClickedEvent

class Button(caption: String, style : String = "") extends Span(List(new Text(caption)), "btn "+style)
{

}
