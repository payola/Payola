package cz.payola.web.client.presenters.components

import cz.payola.common.entities.Plugin
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.events.SimpleUnitEvent
import cz.payola.web.client.views.elements.Anchor
import cz.payola.web.client.views.elements.lists._
import cz.payola.web.client.views.elements.form.fields.TextInput

/**
 * @author Jiri Helmich
 */
class DataCubeDialog() extends Modal("Create a DataCube Vocabulary plugin", Nil)
{

    val dcvUrlField = new InputControl[TextInput]("Vocabulary URL", new TextInput("url", "", "Vocabulary URL"), None, None)
    val placeholder = new Div(List(dcvUrlField))

    override val body = List(placeholder)
}
