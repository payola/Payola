package cz.payola.web.client.views.bootstrap

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.View
import cz.payola.web.client.views.elements.lists._
import cz.payola.web.client.views.elements.form.fields.TextInput
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.elements.form.Field
import collection.mutable
import collection.mutable.ListBuffer
import scala.Some
import s2js.adapters.html
import cz.payola.common.entities.settings.LabelItem
import cz.payola.web.client.presenters.entity.settings.ClassCustomizationEventArgs

class OrderedItemsList(name: String, listElements: Seq[LabelItem] = Nil, cssClass: String = "")
    extends ComposedView with Field[Option[String]] {

    private var parentHtmlElement: Option[html.Element] = None

    val allListItems: mutable.Buffer[OrderedListItem] =
        ListBuffer[OrderedListItem]() ++ {
            if(!listElements.exists(_.userDefined)) { //conditionally add editable field
                val item = new OrderedListItem(new LabelItem("", true, false))
                item.textArea.changed += { e =>
                    this.changed.triggerDirectly(this)
                    false
                }
                List(item)
            } else {
                List[OrderedListItem]()
            }
        } ++ listElements.map{element =>
            new OrderedListItem(element)
        }

    allListItems.foreach{ listItem =>
        listItem.buttonDown.mouseClicked += { e =>
            val index = allListItems.indexOf(listItem)
            if (index != allListItems.length) {
                allListItems.remove(index)
                allListItems.insert(index + 1, listItem)
                update()
                changed.triggerDirectly(this)
            }
            false
        }
        listItem.buttonUp.mouseClicked += { e =>
            val index = allListItems.indexOf(listItem)
            if (index != 0) {
                allListItems.remove(index)
                allListItems.insert(index - 1, listItem)
                update()
                changed.triggerDirectly(this)
            }
            false
        }
        listItem.buttonAccepted.mouseClicked += { e =>
            changed.triggerDirectly(this)
            false
        }
        listItem.buttonForbidden.mouseClicked += { e =>
            changed.triggerDirectly(this)
            false
        }
        listItem.textArea.changed += { e =>
            if(listItem.labelItem.userDefined)
                changed.triggerDirectly(this)
            false
        }
    }

    val itemsList = new UnorderedList(allListItems, cssClass+" nav nav-list")

    def createSubViews: Seq[View] = {

        List(new Div(List(itemsList),
            "modal-inner-view well no-padding").setAttribute("style", "height:100%; padding: 8px 0;"))
    }

    def formHtmlElement = allListItems(0).formHtmlElement

    def value = {
        var result = ""
        allListItems.foreach{ item =>
            if(item.value.isDefined) {
                if(item.accepted) { result += "T" } else { result += "F" }
                result += { if(item.labelItem.userDefined) { "U" } else { "" } } + "-" + item.value.get + ";"
            } else {
                result += ";"
            }
        }
        Some(result)
    }

    def isActive = allListItems(0).isActive

    def isActive_=(value: Boolean) {
        allListItems(0).isActive = value
    }

    protected def updateValue(newValue: Option[String]) {
        if (newValue.isDefined) {
            val values: Array[String] = newValue.get.split(";")

            for(pointer <- 0 to values.length) {
                allListItems(pointer).updateValue(Some(values(pointer)))
            }
        }
    }

    private def update() {
        if(parentHtmlElement.isDefined) {
            this.destroy()
            this.render(parentHtmlElement.get)
        }
    }

    override def render(parent: html.Element) {
        super.render(parent)
        parentHtmlElement = Some(parent)
    }
}



class OrderedListItem(val labelItem: LabelItem)
    extends ComposedView with Field[Option[String]] {

    val _buttonUp = new Anchor(List(new Icon(Icon.arrow_up)))
    val _buttonDown = new Anchor(List(new Icon(Icon.arrow_down)))
    val _buttonAccepted = new Anchor(List(new Icon(Icon.ok)))
    val _buttonForbidden = new Anchor(List(new Icon(Icon.remove)))

    var accepted = labelItem.accepted

    if(labelItem.accepted) {
        _buttonForbidden.hide()
        _buttonAccepted.show("inline")
    } else {
        _buttonAccepted.hide()
        _buttonForbidden.show("inline")
    }

    val _textArea = new TextInput(if(labelItem.userDefined) { "U" } else { "" }, labelItem.value)
    if(!labelItem.userDefined) {
        textArea.disable()
    }

    val listItem = new ListItem(List(new Div(List(_buttonUp, _buttonDown, _textArea, _buttonAccepted, _buttonForbidden)).setAttribute(
        "style", "inline-block")))

    _buttonAccepted.mouseClicked += { e =>
        buttonAccepted.hide()
        buttonForbidden.show("inline")

        accepted = false
        false
    }
    _buttonForbidden.mouseClicked += { e =>
        buttonForbidden.hide()
        buttonAccepted.show("inline")

        accepted = true
        false
    }

    def textArea = _textArea

    def buttonUp = _buttonUp

    def buttonDown = _buttonDown

    def buttonAccepted = _buttonAccepted

    def buttonForbidden = _buttonForbidden

    def createSubViews(): Seq[View] = {
        List(listItem)
    }

    def formHtmlElement = textArea.htmlElement

    def value: Option[String] = {
        Some(textArea.value)
    }

    def updateValue(newValue: Option[String]) {
        textArea.updateValue(newValue.map(_.toString).getOrElse(""))
    }

    def isActive = textArea.isActive

    def isActive_=(newValue: Boolean) {
        textArea.isActive = newValue
    }
}