package cz.payola.web.client.mvvm

import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.Element

/**
 *
 * @author jirihelmich
 * @created 4/17/12 2:27 PM
 * @package cz.payola.web.client.mvvm_api
 */

trait Component
{
    def render(parent: Element = document.body)
}
