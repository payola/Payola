package cz.payola.web.client.presenters.components

import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.entity.analysis.ReadOnlyAnalysisVisualizer
import cz.payola.web.client.views.elements.Div
import cz.payola.common.entities.Analysis
import collection.mutable.ArrayBuffer
import s2js.adapters.html
import s2js.adapters.browser._
import scala.Some

class AnalysisParamSelectorDialog(analysis: Analysis)
    extends Modal("Choose analysis params to be dynamic", Nil, Some("OK"), None, false, "preview-dialog")
{
    val paramIds = new ArrayBuffer[String]

    val visualizer = new ReadOnlyAnalysisVisualizer(analysis)
    visualizer.paramNameClicked += { eventArg => paramIds += eventArg.target.id }

    val placeholder = new Div(List())

    override val body = List(placeholder)

    override def render(parent: html.Element = document.body) {
        super.render(parent)
        visualizer.render(placeholder.blockHtmlElement)
    }
}
