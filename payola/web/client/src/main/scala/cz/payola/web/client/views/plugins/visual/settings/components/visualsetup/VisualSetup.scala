package cz.payola.web.client.views.plugins.visual.settings.components.visualsetup

import cz.payola.web.client.views._
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.elements.{Anchor, ListItem, Text}
import cz.payola.web.client.events._
import cz.payola.web.client.views.plugins.visual.settings.{TextSettingsModel, EdgeSettingsModel, VertexSettingsModel}
import cz.payola.web.client.views.events._
import s2js.adapters.js.dom.Element

class VisualSetup(
    var vertexModel: VertexSettingsModel,
    var edgesModel: EdgeSettingsModel,
    var textModel: TextSettingsModel)
    extends ComposedComponent
{
    val settingsChanged = new SimpleEvent[VisualSetup]

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

    def createSubComponents = {
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
}
