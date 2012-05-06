package cz.payola.web.client.mvvm_api

import s2js.adapters.js.browser
import s2js.adapters.js.browser.document
import collection.mutable.ArrayBuffer
import cz.payola.web.client.events.{ClickedEventArgs, EventArgs}
import s2js.adapters.js.dom.{Element, Node}

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
