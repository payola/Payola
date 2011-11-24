package s2js.adapters.goog.editor.plugins

import s2js.adapters.js.dom.Element
import s2js.adapters.goog.dom.DomHelper
import s2js.adapters.goog.ui.editor.LinkDialog
import s2js.adapters.goog.editor.{Plugin, Link}

abstract class AbstractBubblePlugin extends Plugin
{
    def getBubbleTargetFromSelection(selectedElement: Element): Element = null

    def getBubbleType(): String = null

    def getBubbleTitle(): String = null

    def createBubbleContents(bubbleContainer: Element) {}
}

class AbstractDialogPlugin extends Plugin

class EnterHandler extends Plugin

class BasicTextFormatter extends Plugin
{
    def execCommandHelper_(command: String, value: String = "", preserveDir: Boolean = false, styleWithCss: Boolean = false) {}
}

class HeaderFormatter extends Plugin

class TableEditor extends Plugin

class RemoveFormatting extends Plugin

class Blockquote(requiresClassNameToSplit: Boolean) extends Plugin

class LinkBubble extends AbstractBubblePlugin

class LinkDialogPlugin extends AbstractDialogPlugin
{
    def createDialog(dialogDomHelper: DomHelper, link: Link): LinkDialog = null
}

class TagOnEnterHandler(tag: String) extends EnterHandler
