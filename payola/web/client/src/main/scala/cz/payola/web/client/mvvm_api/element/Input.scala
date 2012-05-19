package cz.payola.web.client.mvvm_api.element

import s2js.adapters.js.browser.document
import s2js.adapters.js.dom
import cz.payola.web.client.mvvm_api.Component
import dom.Element
import cz.payola.web.client.events._

/**
 *
 * @author jirihelmich
 * @created 4/17/12 2:35 PM
 * @package cz.payola.web.client.mvvm_api.element
 */

class Input(val name: String, val value: String, val title: Option[String], val addClass: String = "")
    extends Component {

    //require(document.getElementById(name) == null)

    val changed = new ChangedEvent[Input]
    val clicked = new ClickedEvent[Input]

    //val label = document.createElement[Label]()
    val field = document.createElement[dom.Input]("input")
    field.setAttribute("name",name)
    field.setAttribute("id",name)
    field.setAttribute("type","text")
    field.setAttribute("class", addClass)
    if(title.isDefined) {
        field.setAttribute("title", title.get)
    }
    field.value = value

    field.onkeyup = {
        event => changed.trigger(new ChangedEventArgs(this))
    }

    field.onclick = {
        event => clicked.trigger(new ClickedEventArgs(this))
    }

    def setMaxLength(length: Int) {
        field.setAttribute("maxlength", length.toString())
    }

    def getMaxLength : Int = {
        field.getAttribute("maxlength").toInt
    }

    def render(parent: Element) {
        //TODO: also consider using the twitter-bootstrap wrapHTML
        //parent.appendChild(label)
        parent.appendChild(field)
    }

    def getText : String = {
        field.value
    }

    def setText(value : String) {
        field.value = value

        changed.trigger(new ChangedEventArgs(this))
    }
}
