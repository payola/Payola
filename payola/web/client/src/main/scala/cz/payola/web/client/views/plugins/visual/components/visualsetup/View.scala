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

class View extends Component
{
    private val textBox = new Input("color")

    setEventHandlers()

    def render(parent: Node = document.body) {
        textBox.render(parent)
    }

    private def setEventHandlers() = {
        textBox.changed += {
            e => window.alert(e.target.getText)
        }
    }
}
