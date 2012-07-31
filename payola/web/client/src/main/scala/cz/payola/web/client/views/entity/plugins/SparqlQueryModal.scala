package cz.payola.web.client.views.entity.plugins

import cz.payola.web.client.views.bootstrap.Modal
import cz.payola.web.client.views.elements.TextArea

class SparqlQueryModal extends Modal("Execute SPARQL Query", Nil, Some("Execute"))
{
    val sparqlQueryInput = new TextArea("", "", Some("CONSTRUCT or SELECT SPARQL Query"),
        "textarea-fixed").setAttribute("style", "width: 98%; height: 300px;")

    override val body = List(sparqlQueryInput)
}
