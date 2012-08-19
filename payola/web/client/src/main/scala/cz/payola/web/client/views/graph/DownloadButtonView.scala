package cz.payola.web.client.views.graph

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.elements.lists.ListItem

class DownloadButtonView extends ComposedView
{
    val rdfDownloadAnchor = new Anchor(List(new Text("Download As RDF/XML")))

    val ttlDownloadAnchor = new Anchor(List(new Text(" Download As TTL")))

    val downloadButton = new DropDownButton(List(
        new Icon(Icon.download),
        new Text(" Download  ")
    ),
        List(
            new ListItem(List(rdfDownloadAnchor)),
            new ListItem(List(ttlDownloadAnchor))
        )
    )

    def createSubViews = List(downloadButton)
}
