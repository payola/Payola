package cz.payola.web.client.mvvm_api.element

import cz.payola.web.client.mvvm_api.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.{Element, Node}

/**
 *
 * @author jirihelmich
 * @created 5/4/12 8:16 PM
 * @package cz.payola.web.client.mvvm_api.element
 */

class Text(val value: String) extends Component
{
    def render(parent: Element) = {
        parent.innerHTML = value
    }
}
