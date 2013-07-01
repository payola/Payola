package cz.payola.web.client.views.graph.sigma

import properties._
import cz.payola.common.entities.settings._
import s2js.adapters.js.sigma._

object PropertiesSetter {

    def updateEdge(propertyCustomization: Option[PropertyCustomization], edge: Edge) {

        val colorOpt = if(propertyCustomization.isDefined) Some(propertyCustomization.get.strokeColor) else None

        if(colorOpt.isDefined && colorOpt.get != null && colorOpt.get != "") {
            edge.color = colorOpt.get
        } else {
            edge.color = EdgeProperties.color
        }
    }

    def updateNode(classCustomization: Option[ClassCustomization], node: Node) {

        val colorOpt = if(classCustomization.isDefined) Some(classCustomization.get.fillColor) else None

        if(colorOpt.isDefined && colorOpt.get != null && colorOpt.get != "") {
            node.color = colorOpt.get
        } else {
            node.color = NodeProperties.color
        }
    }
}
