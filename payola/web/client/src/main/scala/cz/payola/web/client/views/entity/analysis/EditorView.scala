package cz.payola.web.client.views.entity.analysis

import cz.payola.web.client.views.ComposedView
import cz.payola.common.entities.Analysis

/**
 *
 */
class EditorView(analysis: Analysis) extends ComposedView
{
    val toolbarView = new EditorToolbarView
    val canvasView = new EditorCanvasView

    def createSubViews = List(toolbarView, canvasView)
}
