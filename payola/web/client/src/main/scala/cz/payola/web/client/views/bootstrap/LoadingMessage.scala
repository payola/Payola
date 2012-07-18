package cz.payola.web.client.views.bootstrap

import cz.payola.web.client.views.elements._

class LoadingMessage(message: String) extends Div()
{
    val heading = new Heading(List(new Text(message)))

    val progressBar = new Div(Nil, "bar")

    heading.setAttribute("style", "padding: 20px;")
    progressBar.setAttribute("style", "width: 100%;")

    override val innerViews = List(
        new Div(List(heading), "row-fluid"),
        new Div(List(new Div(List(progressBar), "progress progress-striped active")), "row-fluid")
    )
}
