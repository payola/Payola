package cz.payola.web.client.views.plugins.visual.components.visualsetup

import cz.payola.web.client.views.plugins.visual.Color
import s2js.adapters.js.browser.document

/**
 *
 * @author jirihelmich
 * @created 4/17/12 2:06 PM
 * @package cz.payola.web.client.views.plugins.visual.components.visualsetup
 */

class ViewModel
{
    val view = new View
    val model = new Model(Color.Black)

    view.render(document.getElementById("controls"))
}
