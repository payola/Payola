package cz.payola.web.client.presenters.components

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._

class ShareButton(isPublic: Boolean) extends ComposedView
{
    private var isPublic_ : Boolean = false

    private val TEXT_IS_PUBLIC = "Make private"
    private val TEXT_IS_NOT_PUBLIC = "Make public"

    val shareToGroupLink = new Anchor(List(new Icon(Icon.group), new Text(" To group")), "#")
    private val groupItem = new ListItem(List(shareToGroupLink))

    val shareToUserLink = new Anchor(List(new Icon(Icon.user), new Text(" To user")), "#")
    private val userItem = new ListItem(List(shareToUserLink))

    private val divider = new ListItem(List(), "divider")

    private val publicLinkCaption = new Text("")
    val makePublicLink = new Anchor(List(new Icon(Icon.globe), new Text(" "), publicLinkCaption), "#")
    private val publicItem = new ListItem(List(makePublicLink))

    private val dropDownButton = new DropDownButton(List(new Icon(Icon.share, true), new Text(" Share")),
        List(groupItem, userItem, divider, publicItem), "btn-info")

    setIsPublic(isPublic)

    def createSubViews = List(dropDownButton)

    def setIsPublic(isPublic: Boolean = true){
        isPublic_ = isPublic
        if (isPublic){
            publicLinkCaption.text_=(TEXT_IS_PUBLIC)
        }else{
            publicLinkCaption.text_=(TEXT_IS_NOT_PUBLIC)
        }
    }

    def getIsPublic = isPublic_
}
