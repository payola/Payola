package cz.payola.web.client.views.plugins.visual.components.visualsetup

import cz.payola.web.client.views.plugins.visual.Color
import s2js.adapters.js.dom.Element

/**
 *
 * @author jirihelmich
 * @created 4/17/12 2:06 PM
 * @package cz.payola.web.client.views.plugins.visual.components.visualsetup
 */

class InputViewModel(parentElement: Element, elementName: String, value: Any, whereToBind: String)
{
    val view = new InputView("input", whereToBind, value.toString)
    val model = value match {
        case i: Color =>
            new Model[Color](i)
        case i: Double =>
            new Model[Double](i)
        case i: String =>
            new Model[String](i)
        case _ =>
            new Model[Any](value)
    }

    render()

    def render() {
        view.render(parentElement)
    }
}
