package cz.payola.web.client.views.bootstrap

import cz.payola.web.client.View
import cz.payola.web.client.views.elements._

class DropDownButtonWithCaret(anchorViews: Seq[View], toggleAnchorViews: Seq[View], items: Seq[ListItem], buttonCssClass: String = "")
    extends DropDownButton(toggleAnchorViews, items, buttonCssClass)
{
    val anchor = new Anchor(anchorViews, "#", "btn dropdown-toggle " + buttonCssClass)

    override val innerViews = List(anchor, toggleAnchor, new UnorderedList(items, "dropdown-menu"))
}
