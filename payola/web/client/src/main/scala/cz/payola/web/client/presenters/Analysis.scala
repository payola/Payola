package cz.payola.web.client.presenters

import cz.payola.web.client.mvvm_api.element._
import cz.payola.web.client.presenters.components.AnalysisControls
import s2js.adapters.js.browser.document

/**
  *
  * @author jirihelmich
  * @created 5/23/12 11:20 PM
  * @package cz.payola.web.client.presenters
  */

class Analysis(elementToDrawIn: String) extends Index(elementToDrawIn)
{
    val controls = new AnalysisControls
    controls.render(document.getElementById("analysis-controls"))
}
