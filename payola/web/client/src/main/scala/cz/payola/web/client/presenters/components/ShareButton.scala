package cz.payola.web.client.presenters.components

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.events._

class ShareButton(isPublic: Boolean) extends ComposedView
{
    private var isPublic_ : Boolean = false

    val clicked = new SimpleUnitEvent[ShareButton]
    dropDownButton.anchor.mouseClicked += {e =>
        clicked.triggerDirectly(this)
        false
    }

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

    private val textIsPublic = new Text(" ")
    private val dropDownButton = new DropDownButtonWithCaret(List(textIsPublic),List(new Icon(Icon.share, true),new Text("Share")),
        List(groupItem, userItem, divider, publicItem), "btn-warning")

    setIsPublic(isPublic)

    def setActive(isActive: Boolean = true){
        if (isActive){
            dropDownButton.toggleAnchor.addCssClass("disabled")
            dropDownButton.anchor.addCssClass("disabled")
        }else{
            dropDownButton.toggleAnchor.removeCssClass("disabled")
            dropDownButton.anchor.removeCssClass("disabled")
        }
    }

    def createSubViews = List(dropDownButton)

    def setIsPublic(isPublic: Boolean = true){
        isPublic_ = isPublic
        if (isPublic){
            publicLinkCaption.text_=(TEXT_IS_PUBLIC)
            textIsPublic.text_=(" Public")
            dropDownButton.anchor.removeCssClass("btn-warning")
            dropDownButton.anchor.addCssClass("btn-success")
        }else{
            publicLinkCaption.text_=(TEXT_IS_NOT_PUBLIC)
            textIsPublic.text_=(" Private")
            dropDownButton.anchor.addCssClass("btn-warning")
            dropDownButton.anchor.removeCssClass("btn-success")
        }
    }

    def getIsPublic = isPublic_
}
