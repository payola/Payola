package cz.payola.web.client.views.plugins.visual.components.visualsetup

import cz.payola.web.client.mvvm_api.Component
import s2js.adapters.js.dom.Node
import cz.payola.web.client.mvvm_api.element.Input
import s2js.adapters.js.browser.window
import s2js.adapters.js.browser.document

/**
 *
 * @author jirihelmich
 * @created 4/17/12 2:06 PM
 * @package cz.payola.web.client.views.plugins.visual.components.visualsetup
 */

class InputView(name: String, whereToBind: String, value: String) extends Component
{
    private val textBox = new Input(name, value)

    setEventHandlers()

    def render(parent: Node = document.body) {
        textBox.render(parent)
    }

    private def setEventHandlers() = { //TODO shouldn't be the handlers setted from the outside (as a construction parameter or via a add method)?
        textBox.changed += {
            e => window.localStorage.setItem(whereToBind, e.target.getText)
            //this.valuewindow.alert(e.target.getText)
                true
        }
        /*textBox.clicked += {
            e => window.alert(e.target.getText)
        }*/
    }
}
