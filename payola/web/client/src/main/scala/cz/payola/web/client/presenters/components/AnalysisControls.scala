package cz.payola.web.client.presenters.components

import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.elements.Div
import cz.payola.web.client.views.elements.Anchor
import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.web.client.views.bootstrap.inputs.TextInputControl

class AnalysisControls(timeoutSeconds: Long) extends ComposedView
{
    private val icon = new Italic(List(), "icon-play icon-white")

    val runBtnCaption = new Text("Run Analysis")

    val runBtn = new Anchor(List(icon, runBtnCaption), "#", "btn btn-success span2")

    val progressValueBar = new Div(List(), "bar")

    progressValueBar.setAttribute("style", "width: 0%; height: 40px")

    val progressDiv = new Div(List(progressValueBar), "progress progress-striped progress-success active span5")

    val stopButton = new Button(new Text("Stop"), "btn-danger disabled span2", new Icon(Icon.stop, true))

    val timeoutControl = new TextInputControl("Set evaluation timeout [sec.]:", "timeout", timeoutSeconds.toString(),
        "Set timeout", "span3 timeout-control")

    timeoutControl.input.addCssClass("span2")

    private val timeoutInfoCaptionPre = new Text("The evaluation will timeout in [sec.]: ")
    val timeoutInfo = new Text(timeoutSeconds.toString)

    val timeoutInfoBar = new Span(List(timeoutInfoCaptionPre, timeoutInfo), "span3 none")

    private val wrap = new Div(List(runBtn, stopButton, progressDiv, timeoutControl, timeoutInfoBar), "well analysis-controls")

    def createSubViews = List(wrap)
}
