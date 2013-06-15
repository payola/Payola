package cz.payola.web.client.presenters.components

import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.entity.analysis.ReadOnlyAnalysisVisualizer
import cz.payola.web.client.views.elements.Div
import cz.payola.common.entities.Analysis
import collection.mutable.ArrayBuffer
import s2js.adapters.html
import s2js.adapters.browser._
import scala.Some
import cz.payola.web.client.views.elements.form.fields.TextInput
import scala.collection.immutable.List
import scala.collection.immutable.List

class PromptDialog(label: String)
    extends Modal("Prompt", Nil, Some("OK"), None, false)
{

    val prompt = new TextInput("result", "", label)

    val placeholder = new Div(List(prompt))

    override val body = List(placeholder)
}
