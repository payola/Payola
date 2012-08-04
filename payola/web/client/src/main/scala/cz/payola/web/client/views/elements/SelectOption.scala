package cz.payola.web.client.views.elements

import s2js.adapters.js.html
import cz.payola.web.client.views.ElementView
import s2js.adapters.html.elements.Option

class SelectOption(text: String, value: String)
    extends ElementView[html.elements.Option]("option", Nil, "")
{
    htmlElement.text = text
    htmlElement.value = value
}
