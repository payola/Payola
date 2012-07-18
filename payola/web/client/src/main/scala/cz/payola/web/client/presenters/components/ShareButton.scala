package cz.payola.web.client.presenters.components

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._

class ShareButton extends ComposedView
{
    val groupItem = new ListItem(List(new Anchor(List(new Icon(Icon.group), new Text(" To group")), "#")))
    val userItem = new ListItem(List(new Anchor(List(new Icon(Icon.user), new Text(" To user")), "#")))
    val divider = new ListItem(List(),"divider")
    val publicItem = new ListItem(List(new Anchor(List(new Icon(Icon.globe), new Text(" Make public")),"#")))
    val items = List(groupItem, userItem, divider, publicItem)

    private val dropDownButton = new DropDownButton(List(new Icon(Icon.share, true), new Text(" Share")), items, "btn-info")

    def createSubViews = List(dropDownButton)
}
