package cz.payola.web.client.presenters.components

import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.elements.Div
import cz.payola.web.client.views.elements.Anchor
import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.elements.form.fields._
import cz.payola.web.shared.Payola

class AnalysisControls() extends ComposedView
{

    val runBtnCaption = new Text("Run Analysis")
    private val runBtnIcon = new Icon(Icon.play, true)
    val runBtn = new Button(runBtnCaption, "btn btn-success span2", runBtnIcon)

    val progressBar = new ProgressBar()
    val stopButton = new Button(new Text("Stop"), "btn-danger disabled span2", new Icon(Icon.stop, true))
    stopButton.setIsEnabled(false)

    private val timeoutInfoCaptionPre = new Text("Elapsed [sec.]: ")
    val timeoutInfo = new Text("0")
    val timeoutInfoBar = new Span(List(timeoutInfoCaptionPre, timeoutInfo), "none span3").setAttribute("style", "width: 100%; height: 20px;")

    private val wrap = new Div(List(runBtn, stopButton, progressBar,
        new Table(List(
            new TableRow(List(new TableCell(List(timeoutInfoBar))))))),
        "well analysis-controls").setAttribute("style", "min-height: 60px;")

    def createSubViews = List(wrap)
}
