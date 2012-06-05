package cz.payola.web.client.views.plugins.visual.techniques.tree

import cz.payola.web.client.views.plugins.visual.animation.Animation
import cz.payola.web.client.views.plugins.visual.techniques.BaseTechnique
import cz.payola.web.client.views.plugins.visual.settings.components.visualsetup.VisualSetup
import cz.payola.web.client.views.plugins.visual.graph.{Component, VertexView}
import collection.mutable.ListBuffer
import cz.payola.web.client.views.plugins.visual.Point

/**
  * Visual plug-in technique that places the vertices into a tree structure.
  */
class TreeTechnique(settings: VisualSetup) extends BaseTechnique(settings)
{
    protected def getTechniquePerformer(component: Component, animate: Boolean): Animation[ListBuffer[(VertexView, Point)]] = {

        if(animate) {
            basicTreeStructure(component.vertexViews, None, redrawQuick, redraw, None)
        } else {
            basicTreeStructure(component.vertexViews, None, redrawQuick, redraw, Some(0))
        }
    }

    override def clean() {
        super.clean()
    }

    def getName:String = {
        "tree visualisation"
    }
}
