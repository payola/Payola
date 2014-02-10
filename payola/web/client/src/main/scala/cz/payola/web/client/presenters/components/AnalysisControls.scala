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
    val runBtn = new Button(runBtnCaption, "btn btn-success", runBtnIcon)

    val progressBar = new ProgressBar()
    val stopButton = new Button(new Text("Stop"), "btn-danger disabled", new Icon(Icon.stop, true))
    stopButton.setIsEnabled(false)

    private val timeoutInfoCaptionPre = new Text("Elapsed [sec.]: ")
    val timeoutInfo = new Text("0")

    val btnDiv = new Div(List(runBtn, stopButton),"col-lg-3")
    val progressDiv = new Div(List(progressBar), "col-lg-6")
    val timeoutInfoBar = new Span(List(timeoutInfoCaptionPre, timeoutInfo), "none col-lg-3")

    private val wrap = new Div(List(btnDiv, progressDiv, timeoutInfoBar), "analysis-controls panel-body")
    private val panel = new Div(List(wrap), "panel panel-default")

    def createSubViews = List(panel)
}
