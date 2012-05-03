package cz.payola.web.client.mvvm_api.element

import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.Node
import s2js.adapters.js.dom
import cz.payola.web.client.mvvm_api.Component
import collection.mutable.ArrayBuffer
import s2js.adapters.js.browser.window
import cz.payola.web.client.events.{ClickedEvent, ChangedEvent}

/**
 *
 * @author jirihelmich
 * @created 4/17/12 2:35 PM
 * @package cz.payola.web.client.mvvm_api.element
 */

class Input(val name: String, val value: String, val addClass: String = "") extends Component
{
    //require(document.getElementById(name) == null)

    val changed = new ArrayBuffer[ChangedEvent[Input] => Boolean]()
    val clicked = new ArrayBuffer[ClickedEvent[Input] => Boolean]()

    //val label = document.createElement[Label]()
    val field = document.createElement[dom.Input]("input")
    field.setAttribute("name",name)
    field.setAttribute("id",name)
    field.setAttribute("type","text")
    field.setAttribute("class", addClass)
    field.value = value

    field.onkeyup = {
        event => notify(changed, new ChangedEvent[Input](this))
    }

    field.onclick = {
        event => notify(clicked, new ClickedEvent[Input](this))
    }

    def setMaxLength(length: Int) {
        field.setAttribute("maxlength", length.toString())
    }

    def getMaxLength : Int = {
        field.getAttribute("maxlength").toInt
    }

    def render(parent: Node) {
        //TODO: also consider using the twitter-bootstrap wrapHTML
        //parent.appendChild(label)
        parent.appendChild(field)
    }

    def getText : String = {
        field.value
    }

    def setText(value : String) {
        field.value = value

        notify(changed, new ChangedEvent[Input](this))
    }
}
