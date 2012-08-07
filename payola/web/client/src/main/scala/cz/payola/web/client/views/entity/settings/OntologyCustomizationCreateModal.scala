package cz.payola.web.client.views.entity.settings

import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.elements.form.fields.TextInput

class OntologyCustomizationCreateModal extends Modal("Create a new ontology customization", Nil, Some("Create"))
{
    val name = new InputControl("Name", new TextInput("customizationName", "", ""))

    val url = new InputControl("Ontology URL", new TextInput("ontologyUrl", "http://", ""))

    override val body = List(name, url)
}
