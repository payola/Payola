package cz.payola.web.client.views.bootstrap.modals

import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Modal

object AlertModal {
    def runModal(alertDescription: String, additionalCSSClass: String = "") {
        val alert = new AlertModal(alertDescription, additionalCSSClass)
        alert.render()
    }
}

class AlertModal(alertDescription: String, additionalCSSClass: String = "")
    extends Modal("Alert", Nil, Some("OK"), None, false)
{
    val description = new PreFormatted(alertDescription, "pre-scrollable")
    description.setAttribute("style", "height: 300px;")

    override val body = List(
        new Div(List(
            new Text(
                alertDescription
            )),
            "alert " + additionalCSSClass
        )
    )
}

