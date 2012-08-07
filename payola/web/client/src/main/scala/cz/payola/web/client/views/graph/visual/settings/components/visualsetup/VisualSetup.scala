package cz.payola.web.client.views.graph.visual.settings.components.visualsetup

import cz.payola.web.client.views._
import cz.payola.web.client.views.elements.{Anchor, ListItem, Text}
import cz.payola.web.client.events._
import cz.payola.web.client.views.graph.visual.settings._
import cz.payola.common.entities.settings.OntologyCustomization

class VisualSetup(
    var vertexModel: VertexSettingsModel,
    var edgesModel: EdgeSettingsModel,
    var textModel: TextSettingsModel)
    extends ComposedView
{
    val settingsChanged = new SimpleUnitEvent[VisualSetup]

    def createSubViews = {
        List()
    }

    private def settingsChangedHandler(e: EventArgs[_]): Boolean = {
        settingsChanged.triggerDirectly(this)
        true
    }

    def setOntologyCustomization(customization: Option[OntologyCustomization]) {
        vertexModel.setOntologyCustomization(customization)
        edgesModel.setOntologyCustomization(customization)
        textModel.setOntologyCustomization(customization)
    }
}
