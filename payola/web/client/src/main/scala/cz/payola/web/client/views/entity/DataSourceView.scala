package cz.payola.web.client.views.entity

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._

class DataSourceView(dataSourceName: String) extends ComposedView
{
    val heading = new Heading(List(new Text("Data source browser: " + dataSourceName)), 2)

    val backButton = new Button(new Icon(Icon.arrow_left))

    val nextButton = new Button(new Icon(Icon.arrow_right))

    val nodeUriInput = new Input("nodeUri", "", Some("Node URI"), "input-large")

    val goButton = new Button(new Text("Go!"))

    val navigation = new Form(List(backButton, nextButton, nodeUriInput, goButton), "form-inline pull-right")

    val graphViewSpace = new Div(Nil, "row-fluid")

    def createSubViews = {
        List(
            new Div(List(
                new Div(List(heading), "span8"),
                new Div(List(navigation), "span4")),
                "row-fluid"
            ),
            graphViewSpace
        )
    }
}
