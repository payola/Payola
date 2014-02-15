package cz.payola.web.client.views.entity.settings

import cz.payola.web.client.views.bootstrap._
import scala.Some
import cz.payola.web.client.views.elements.form.fields.TextInput

class UserCustomizationCreateModal extends Modal("Create a new user customization", Nil, Some("Create"))
{
    val name = new InputControl("Name",
        new TextInput("customizationName", "", "").setAttribute("style", "margin-bottom:55px;"),
        Some("col-lg-2"), Some("col-lg-10"))

    override val body = List(name)
}