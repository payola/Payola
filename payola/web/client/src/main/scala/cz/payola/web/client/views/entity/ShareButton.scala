package cz.payola.web.client.views.entity

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._

class ShareButton(private var _isPublic: Boolean) extends ComposedView
{
    val shareToGroupButton = new Anchor(List(new Icon(Icon.group), new Text(" To group")))

    val shareToUserButton = new Anchor(List(new Icon(Icon.user), new Text(" To user")))

    private val makePublicButtonText = new Text("")

    val makePublicButton = new Anchor(List(new Icon(Icon.globe), new Text(" "), makePublicButtonText), "#")

    private val isPublicText = new Text("")

    val dropDownButton = new DropDownButtonWithCaret(
        List(isPublicText),
        List(new Icon(Icon.share, true), new Text("Share")),
        List(
            new ListItem(List(shareToGroupButton)),
            new ListItem(List(shareToUserButton)),
            new ListItem(List(), "divider"),
            new ListItem(List(makePublicButton))
        ),
        "btn-warning"
    )

    isPublic = _isPublic

    def createSubViews = List(dropDownButton)

    def isPublic: Boolean = _isPublic

    def isPublic_=(value: Boolean) {
        _isPublic = value
        if (_isPublic) {
            makePublicButtonText.text_=("Make private")
            isPublicText.text_=(" Public")
            dropDownButton.anchor.removeCssClass("btn-warning")
            dropDownButton.anchor.addCssClass("btn-success")
        } else {
            makePublicButtonText.text_=("Make public")
            isPublicText.text_=(" Private")
            dropDownButton.anchor.addCssClass("btn-warning")
            dropDownButton.anchor.removeCssClass("btn-success")
        }
    }

    def setIsEnabled(isEnabled: Boolean = true) {
        if (isEnabled) {
            dropDownButton.toggleAnchor.removeCssClass("disabled")
            dropDownButton.anchor.removeCssClass("disabled")
        } else {
            dropDownButton.toggleAnchor.addCssClass("disabled")
            dropDownButton.anchor.addCssClass("disabled")
        }
    }
}
