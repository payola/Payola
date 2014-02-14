package cz.payola.web.client.views.entity.settings

import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.elements.form.fields.TextInput

class OntologyCustomizationCreateModal extends Modal("Create a new ontology customization", Nil, Some("Create"))
{
    val name = new InputControl("Name", new TextInput("customizationName", "", ""), Some("col-lg-2"), Some("col-lg-10"))

    val urls = new InputControl("Ontology URLs", new TextInput("ontologyUrls", "http://", ""), Some("col-lg-2"), Some("col-lg-10"))

    override val body = List(name, urls)
}
