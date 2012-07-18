package cz.payola.web.client.presenters.components

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Icon

class ShareButton extends ComposedView
{
    val shareBtn = new Button(new Text("Share"), "btn-info", new Icon(Icon.share, true))
    val dropDown = new Anchor(List(new Span(List(),"caret")),"#","btn btn-info dropdown-toggle")
    dropDown.domElement.setAttribute("data-toggle","dropdown")

    val groupItem = new ListItem(List(new Anchor(List(new Icon(Icon.group), new Text(" To group")), "#")))
    val userItem = new ListItem(List(new Anchor(List(new Icon(Icon.user), new Text(" To user")), "#")))
    val divider = new ListItem(List(),"divider")
    val publicItem = new ListItem(List(new Anchor(List(new Icon(Icon.globe), new Text(" Make public")),"#")))

    val list = new UnorderedList(List(groupItem, userItem, divider, publicItem),"dropdown-menu")

    def createSubViews = List(shareBtn, dropDown, list)
}
