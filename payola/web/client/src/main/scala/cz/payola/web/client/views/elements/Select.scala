package cz.payola.web.client.views.elements

import s2js.adapters.js.html

class Select(options: Seq[SelectOption] = Nil, cssClass: String = "")
    extends FormField[html.elements.Select]("select", options, cssClass)
