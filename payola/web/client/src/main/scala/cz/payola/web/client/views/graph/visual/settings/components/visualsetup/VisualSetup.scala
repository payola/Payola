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

    val vertex = new Anchor(List(new Text("Vertices default style")), "#")

    val vertexOwl = new Anchor(List(new Text("Vertices OWL style")), "#")

    val edges = new Anchor(List(new Text("Edges style")), "#")

    val text = new Anchor(List(new Text("Text style")), "#")

    vertex.mouseClicked += { eventArgs =>
        val modal = new VertexModal(vertexModel)
        modal.saving += settingsChangedHandler _
        modal.render()
        false
    }

    edges.mouseClicked += { eventArgs =>
        val modal = new EdgeModal(edgesModel)
        modal.saving += settingsChangedHandler _
        modal.render()
        false
    }

    text.mouseClicked += { eventArgs =>
        val modal = new TextModal(textModel)
        modal.saving += settingsChangedHandler _
        modal.render()
        false
    }

    def createSubViews = {
        List(
            new ListItem(List(), "divider"),
            new ListItem(List(vertex)),
            new ListItem(List(vertexOwl)),
            new ListItem(List(edges)),
            new ListItem(List(text))
        )
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
