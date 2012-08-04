package cz.payola.web.client.views.bootstrap.modals

import cz.payola.web.client.views.bootstrap.Modal
import cz.payola.web.client.views.elements._

class FatalErrorModal(errorDescription: String)
    extends Modal("Fatal Error!", Nil, None, None, false)
{
    val description = new PreFormatted(errorDescription, "pre-scrollable")

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
