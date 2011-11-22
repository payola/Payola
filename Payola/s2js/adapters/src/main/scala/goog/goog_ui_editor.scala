package s2js.adapters.goog.ui.editor

import s2js.adapters.js.dom.Element
import s2js.adapters.goog.dom.DomHelper
import s2js.adapters.goog.ui._
import s2js.adapters.goog.editor.{Link, Field}
import s2js.adapters.goog.events.{Event, EventTarget}


object DefaultToolbar
{
    def makeToolbar(items: List[AnyRef], elem: Element, isRightToLeft: Boolean = false): Toolbar = null
}

abstract class AbstractDialog(domHelper: DomHelper) extends EventTarget
{
    def createDialogControl(): Dialog
}

object AbstractDialog
{

    class Builder(editorDialog: AbstractDialog)
    {
        def setTitle(title: String): Builder = null

        def setContent(contentElem: Element): Builder = null

        def build(): Dialog = null
    }

}

class LinkDialog(domHelper: DomHelper, link: Link) extends AbstractDialog(domHelper)
{
    val targetLink_ = link

    def createDialogControl(): Dialog = null

    def buildTextToDisplayDiv_() = null

    def buildTabOnTheWeb_() = null

    def buildTabEmailAddress_() = null

    def onChangeTab_(e: Event) {}

    def selectAppropriateTab_(text: String, url: String) {}

    def isNewLink_(): Boolean = false

    def guessUrlAndSelectTab_(text: String) {}

    def setAutogenFlag_(value: Boolean) {}

    def setAutogenFlagFromCurInput_() {}

    def disableAutogenFlag_(autogen: Boolean) {}
}

object LinkDialog
{

    object Id_
    {
        val TEXT_TO_DISPLAY = ""
        val ON_WEB_TAB = ""
        val ON_WEB_INPUT = ""
        val EMAIL_ADDRESS_TAB = ""
        val EMAIL_ADDRESS_INPUT = ""
        val EMAIL_WARNING = ""
        val TAB_INPUT_SUFFIX = ""
    }

}

class ToolbarController(field: Field, toolbar: Toolbar)

object ToolbarFactory
{
    def makeButton(id: String, tooltip: String, caption: String, classNames: String = "", renderer: ButtonRenderer = null, domHelper: DomHelper): Button = null

    def makeToggleButton(id: String, tooltip: String, caption: String, classNames: String = "", renderer: ButtonRenderer = null, domHelper: DomHelper = null): ToggleButton = null
}

class TabPane(dom: DomHelper, opt_caption: String = "") extends Component(dom)
{
    def addTab(id: String, caption: String, tooltip: String, content: Element) {}

    def setSelectedTabId(id: String) {}
}

object messages
{
    val MSG_LINK_CAPTION = ""
    val MSG_EDIT_LINK = ""
    val MSG_TEXT_TO_DISPLAY = ""
    val MSG_LINK_TO = ""
    val MSG_ON_THE_WEB = ""
    val MSG_ON_THE_WEB_TIP = ""
    val MSG_TEST_THIS_LINK = ""
    val MSG_TR_LINK_EXPLANATION = ""
    val MSG_WHAT_URL = ""
    val MSG_EMAIL_ADDRESS = ""
    val MSG_EMAIL_ADDRESS_TIP = ""
    val MSG_INVALID_EMAIL = ""
    val MSG_WHAT_EMAIL = ""
    val MSG_EMAIL_EXPLANATION = ""
    val MSG_IMAGE_CAPTION = ""
}