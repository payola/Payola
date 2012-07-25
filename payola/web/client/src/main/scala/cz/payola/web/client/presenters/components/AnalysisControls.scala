package cz.payola.web.client.presenters.components

import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.elements.Div
import cz.payola.web.client.views.elements.Anchor
import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.presenters.notification.Notification
import cz.payola.web.client.views.bootstrap.modals.AlertModal

class AnalysisControls extends ComposedView
{
    private val icon = new Italic(List(), "icon-play icon-white")
    private val caption = new Text("Run analysis")
    val runBtn = new Anchor(List(icon, caption), "#", "btn btn-primary span2")

    val progressValueBar = new Div(List(),"bar")
    progressValueBar.setAttribute("style", "width: 0%; height: 40px")
    val progressDiv = new Div(List(progressValueBar),"progress progress-striped progress-success active span10")

    private val wrap = new Div(List(runBtn, progressDiv))

    def createSubViews = List(wrap)
}
