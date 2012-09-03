package cz.payola.web.client.views.bootstrap.modals

import cz.payola.web.client.views.bootstrap.Modal
import cz.payola.web.client.views.elements._
import s2js.adapters.browser._

class FatalErrorModal(errorDescription: String)
    extends Modal("Fatal Error!", Nil, Some("Go to Dashboard"), None, false)
{
    val description = new PreFormatted(errorDescription, "pre-scrollable")

    confirming += { e =>
        window.location.href = "/"
        true
    }

    override val body = List(
        new Div(List(
            new Text(
                "The application encountered a fatal error. If the problem doesn't go away after the page refresh, " +
                    "please send us the following error description."
            )),
            "alert alert-error"
        ),
        description
    )
}
