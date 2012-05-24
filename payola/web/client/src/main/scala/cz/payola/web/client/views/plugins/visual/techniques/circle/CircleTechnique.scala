package cz.payola.web.client.views.plugins.visual.techniques.circle

import cz.payola.web.client.views.plugins.visual.animation.Animation
import cz.payola.web.client.views.plugins.visual.techniques.BaseTechnique
import cz.payola.web.client.views.plugins.visual.settings.components.visualsetup.VisualSetup
import cz.payola.web.client.views.plugins.visual.graph.{Component, VertexView}
import collection.mutable.ListBuffer
import s2js.adapters.js.browser.window
import cz.payola.web.client.views.plugins.visual.Point

/**
  * Visual plug-in technique that places the vertices into a circled tree structure.
  */
class CircleTechnique(settings: VisualSetup) extends BaseTechnique(settings)
{
    protected def getTechniquePerformer(component: Component, animate: Boolean): Animation[ListBuffer[(VertexView, Point)]] = {

        if(animate) {
            val flip = new Animation(
                Animation.flipGraph, component.vertexViews, None, redrawQuick, redraw, None)
            basicTreeCircledStructure(component.vertexViews, Some(flip), redrawQuick, redraw, None)
        } else {
            val flip = new Animation(
                Animation.flipGraph, component.vertexViews, None, redrawQuick, redraw, Some(0))
            basicTreeCircledStructure(component.vertexViews, Some(flip), redrawQuick, redraw, Some(0))
        }
    }

    override def clean() {
        super.clean()
    }

    def getName:String = {
        "circle visualisation"
    }
}
