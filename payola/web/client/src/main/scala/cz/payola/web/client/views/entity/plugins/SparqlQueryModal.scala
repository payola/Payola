package cz.payola.web.client.views.entity.plugins

import cz.payola.web.client.views.bootstrap.Modal
import cz.payola.web.client.views.elements.form.fields.TextArea

class SparqlQueryModal extends Modal("Execute SPARQL Query", Nil, Some("Execute"))
{
    val sparqlQueryInput = new TextArea("", "", "Construct or Select SPARQL Query",
        "textarea-fixed").setAttribute("style", "width: 98%; height: 300px;")

    override val body = List(sparqlQueryInput)
}
