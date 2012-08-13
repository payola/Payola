package cz.payola.web.client.views.entity

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.elements.lists.ListItem

class ShareButton(private var _isPublic: Boolean) extends ComposedView
{
    val shareToGroupsButton = new Anchor(List(new Icon(Icon.group), new Text(" To Groups")))

    val shareToUsersButton = new Anchor(List(new Icon(Icon.user), new Text(" To Users")))

    private val makePublicButtonText = new Text("")

    val makePublicButton = new Anchor(List(new Icon(Icon.globe), new Text(" "), makePublicButtonText), "#")

    private val isPublicText = new Text("")

    val dropDownButton = new DropDownButtonWithCaret(
        isPublicText,
        List(new Icon(Icon.share, true), new Text("Share")),
        List(
            new ListItem(List(shareToGroupsButton)),
            new ListItem(List(shareToUsersButton)),
            new ListItem(List(), "divider"),
            new ListItem(List(makePublicButton))
        ),
        "btn-warning"
    ).addCssClass("share-button")

    isPublic = _isPublic

    def createSubViews = List(dropDownButton)

    def isPublic: Boolean = _isPublic

    def isPublic_=(value: Boolean) {
        _isPublic = value
        if (_isPublic) {
            makePublicButtonText.text_=("Make Private")
            isPublicText.text_=(" Public")
            dropDownButton.anchor.removeCssClass("btn-warning")
            dropDownButton.anchor.addCssClass("btn-success")
        } else {
            makePublicButtonText.text_=("Make Public")
            isPublicText.text_=(" Private")
            dropDownButton.anchor.addCssClass("btn-warning")
            dropDownButton.anchor.removeCssClass("btn-success")
        }
    }

    def setIsEnabled(isEnabled: Boolean = true) {
        dropDownButton.toggleAnchor.setIsEnabled(isEnabled)
        dropDownButton.anchor.setIsEnabled(isEnabled)
    }
}
