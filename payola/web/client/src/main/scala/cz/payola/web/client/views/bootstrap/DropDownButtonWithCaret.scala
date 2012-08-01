package cz.payola.web.client.views.bootstrap

import cz.payola.web.client.View
import cz.payola.web.client.views.elements._

class DropDownButtonWithCaret(
    anchorViews: Seq[View],
    toggleAnchorViews: Seq[View],
    _items: Seq[ListItem],
    buttonCssClass: String = "")
    extends DropDownButton(toggleAnchorViews, _items, buttonCssClass)
{
    val anchor = new Anchor(anchorViews, "#", "btn dropdown-toggle " + buttonCssClass)

    override val subViews = List(anchor, toggleAnchor, new UnorderedList(items, "dropdown-menu"))
}
