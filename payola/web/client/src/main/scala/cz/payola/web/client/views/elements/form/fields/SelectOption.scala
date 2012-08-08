package cz.payola.web.client.views.elements.form.fields

import s2js.adapters.html
import cz.payola.web.client.views.ElementView

class SelectOption(text: String, value: String)
    extends ElementView[html.elements.Option]("option", Nil, "")
{
    htmlElement.text = text
    htmlElement.value = value
}
