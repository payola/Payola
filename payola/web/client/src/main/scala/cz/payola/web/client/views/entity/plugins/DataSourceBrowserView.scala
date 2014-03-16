package cz.payola.web.client.views.entity.plugins

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.elements.form.fields._

class DataSourceBrowserView(dataSourceName: String) extends ComposedView
{
    val heading = new Heading(List(new Text("Data source: " + dataSourceName)), 2)

    val nodeUriInput = new TextInput("nodeUri", "", "Node URI", "input-xlarge")

    val goButton = new Button(new Text("Go!"))

    val sparqlQueryButton = new Button(new Text("SPARQL"), "", new Icon(Icon.asterisk))

    val navigation = new Div(List(
        nodeUriInput,
        goButton,
        sparqlQueryButton),
        "form-inline pull-right"
    )

    val graphViewSpace = new Div(Nil, "row")

    def createSubViews = {
        List(
            new Div(List(
                new Div(List(heading), "col-lg-4"),
                new Div(List(navigation), "col-lg-8")),
                "row"
            ),
            graphViewSpace
        )
    }
}
