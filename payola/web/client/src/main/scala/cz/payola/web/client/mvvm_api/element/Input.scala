package cz.payola.web.client.mvvm_api.element

import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.Node
import s2js.adapters.js.dom
import cz.payola.web.client.mvvm_api.Component
import cz.payola.web.client.events.{ChangedEvent}
import collection.mutable.ArrayBuffer

/**
 *
 * @author jirihelmich
 * @created 4/17/12 2:35 PM
 * @package cz.payola.web.client.mvvm_api.element
 */

class Input(val name: String) extends Component
{
    require(document.getElementById(name) == null)

    val changed = new ArrayBuffer[ChangedEvent[Input] => Unit]()

    //val label = document.createElement[Label]()
    val field = document.createElement[dom.Input](name)

    def render(parent: Node) {
        //TODO: also consider using the twitter-bootstrap wrapHTML
        //parent.appendChild(label)
        parent.appendChild(field)
    }

    def getText : String = {
        field.text
    }

    def setText(value : String) {
        field.text = value

        notify(changed, new ChangedEvent[Input](this))
    }
}
