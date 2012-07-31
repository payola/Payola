package cz.payola.web.client.views.entity.settings

import cz.payola.web.client.views.bootstrap.Modal
import cz.payola.web.client.views.bootstrap.inputs.TextInputControl

class OntologyCustomizationCreateModal extends Modal("Create a new ontology customization", Nil, Some("Create"))
{
    val name = new TextInputControl("Name", "customizationName", "", "")

    val url = new TextInputControl("Ontology URL", "ontologyUrl", "http://", "")

    override val body = List(name, url)
}
