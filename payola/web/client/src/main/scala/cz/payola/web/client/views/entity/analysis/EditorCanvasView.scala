package cz.payola.web.client.views.entity.analysis

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Icon

/**
  *
  */
class EditorCanvasView extends ComposedView
 {

     val canvas = new Div(List(),"analysis-canvas")

     def createSubViews = List(canvas)
 }
