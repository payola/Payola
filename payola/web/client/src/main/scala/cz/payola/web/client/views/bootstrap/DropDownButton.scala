package cz.payola.web.client.views.bootstrap

import cz.payola.web.client.View
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.elements.lists._

class DropDownButton(buttonViews: Seq[View], private var _items: Seq[ListItem], buttonCssClass: String = "", divCssClass: String = "")
    extends Div(Nil, divCssClass + " btn-group")
{
    val toggleAnchor = new Button(new Span(buttonViews ++ List(new Span(Nil, "caret"))),
        "btn btn-default dropdown-toggle " + buttonCssClass).setAttribute("data-toggle", "dropdown")

    val menu = new UnorderedList(items, "dropdown-menu")

    override val subViews = List(toggleAnchor, menu)

    def items: Seq[ListItem] = _items

    def items_=(items: Seq[ListItem]) {
        _items.foreach(_.destroy())
        _items = items
        _items.foreach(_.render(menu.htmlElement))
    }

    def setActiveItem(item: ListItem) {
        items.foreach(_.removeCssClass("active"))
        item.addCssClass("active")
    }
}
