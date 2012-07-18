package cz.payola.web.client.views.bootstrap

import cz.payola.web.client.View
import cz.payola.web.client.views.elements._

class DropDownButton(buttonViews: Seq[View], items: Seq[ListItem]) extends Div(Nil, "btn-group")
{
    val toggleAnchor = new Anchor(buttonViews ++ List(new Span(Nil, "caret")), "#", "btn dropdown-toggle")

    toggleAnchor.setAttribute("data-toggle", "dropdown")

    override val innerViews = List(toggleAnchor, new UnorderedList(items, "dropdown-menu"))
}
