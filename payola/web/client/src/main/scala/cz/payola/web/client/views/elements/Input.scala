package cz.payola.web.client.views.elements

import s2js.adapters.js.browser.document
import s2js.adapters.js.dom
import cz.payola.web.client.views.Component
import cz.payola.web.client.views.Component
import dom.Element
import cz.payola.web.client.events._
import s2js.adapters.js.browser.window
import cz.payola.web.client.views.events._

/**
  *
  * @author jirihelmich
  * @created 4/17/12 2:35 PM
  * @package cz.payola.web.client.views_api.element
  */

class Input(val name: String, val value: String, val title: Option[String], val addClass: String = "")
    extends Component
{
    //require(document.getElementById(name) == null)

    val changed = new SimpleEvent[Input]

    val clicked = new SimpleEvent[Input]

    //val label = document.createElement[Label]()
    val field = document.createElement[dom.Input]("input")

    field.setAttribute("name", name)
    field.setAttribute("id", name)
    field.setAttribute("type", "text")
    field.setAttribute("class", addClass)
    if (title.isDefined) {
        field.setAttribute("placeholder", title.get)
        field.setAttribute("title", title.get)
    }
    field.value = value

    field.onkeyup = { e =>
        changed.trigger(this)
        false
    }

    field.onclick = { e =>
        clicked.trigger(this)
        false
    }

    def setMaxLength(length: Int) {
        field.setAttribute("maxlength", length.toString())
    }

    def getMaxLength: Int = {
        field.getAttribute("maxlength").toInt
    }

    def render(parent: Element) {
        //TODO: also consider using the twitter-bootstrap wrapHTML
        //parent.appendChild(label)
        parent.appendChild(field)
    }

    def getText: String = {
        field.value
    }

    def setText(value: String) {
        field.value = value

        changed.trigger(this)
    }

    def getDomElement: Element = {
        field
    }
}
