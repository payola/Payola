package cz.payola.web.client.views.bootstrap

import cz.payola.web.client.View
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.elements.lists._

class DropDownButtonWithCaret(
    anchorView: View,
    toggleAnchorViews: Seq[View],
    _items: Seq[ListItem],
    buttonCssClass: String = "")
    extends DropDownButton(toggleAnchorViews, _items, buttonCssClass)
{
    val anchor = new Button(anchorView, "btn dropdown-toggle " + buttonCssClass)

    override val subViews = List(anchor, toggleAnchor, new UnorderedList(items, "dropdown-menu"))
}
