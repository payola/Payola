package cz.payola.web.client.views.plugins.visual.settings.components.visualsetup

import cz.payola.web.client.views.Component
import cz.payola.web.client.views.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.elements.{Anchor, ListItem, Text}
import cz.payola.web.client.events._
import cz.payola.web.client.views.plugins.visual.settings.{TextSettingsModel, EdgeSettingsModel, VertexSettingsModel}
import cz.payola.web.client.views.events._

class VisualSetup(var vertexModel: VertexSettingsModel, var edgesModel: EdgeSettingsModel,
    var textModel: TextSettingsModel) extends Component
{
    val settingsChanged = new SimpleEvent[VisualSetup]

    val vertex = new Anchor(List(new Text("Vertices default style")), "#")
    val vertexOwl = new Anchor(List(new Text("Vertices OWL style")), "#")
    val edges = new Anchor(List(new Text("Edges style")), "#")
    val text = new Anchor(List(new Text("Text style")), "#")

    val vertexSettings = new VertexModal(vertexModel)
    val edgesSettings = new EdgeModal(edgesModel)
    val textSettings = new TextModal(textModel)

    def render(parent: Node) {
        new ListItem(List(), "divider").render(parent)
        new ListItem(List(vertex)).render(parent)
        new ListItem(List(vertexOwl)).render(parent)
        new ListItem(List(edges)).render(parent)
        new ListItem(List(text)).render(parent)

        vertexSettings.render(document.body)
        edgesSettings.render(document.body)
        textSettings.render(document.body)
    }

    vertexSettings.settingsChanged += {
        evt =>
            settingsChanged.trigger(new EventArgs[VisualSetup](this))
            true
    }
    edgesSettings.settingsChanged += {
        evt =>
            settingsChanged.trigger(new EventArgs[VisualSetup](this))
            true
    }
    textSettings.settingsChanged += {
        evt =>
            settingsChanged.trigger(new EventArgs[VisualSetup](this))
            true
    }

    vertex.mouseClicked += { eventArgs =>
        vertexSettings.show()
        false
    }

    edges.mouseClicked += { eventArgs =>
        edgesSettings.show()
        false
    }

    text.mouseClicked += { eventArgs =>
        textSettings.show()
        false
    }

    vertexOwl.mouseClicked += { eventArgs =>
        false
    }

    def domElement: Element = vertexSettings.domElement
}
