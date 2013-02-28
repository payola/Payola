package cz.payola.web.client.presenters.components

import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.elements.form.fields.TextInput
import cz.payola.web.client.View

class DataCubeDefinitionsDialog(bodyContent: Seq[View]) extends Modal("Choose from the detected definitions", bodyContent, None)
