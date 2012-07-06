package cz.payola.web.client.views.plugins.visual.settings.components.visualsetup

import cz.payola.web.client.mvvm.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.Element
import cz.payola.web.client.mvvm.element.{Anchor, ListItem, Text}
import cz.payola.web.client.events._
import cz.payola.web.client.views.plugins.visual.settings.{TextSettingsModel, EdgeSettingsModel, VertexSettingsModel}

class VisualSetup(var vertexModel: VertexSettingsModel, var edgesModel: EdgeSettingsModel, var textModel: TextSettingsModel) extends Component
{
    val settingsChanged = new ComponentEvent[VisualSetup, EventArgs[VisualSetup]]

    val vertex = new Anchor(List(new Text("Vertices style")), "#")
    val edges = new Anchor(List(new Text("Edges style")), "#")
    val text = new Anchor(List(new Text("Text style")), "#")
    val vertexSettings = new VertexModal(vertexModel)
    val edgesSettings = new EdgeModal(edgesModel)
    val textSettings = new TextModal(textModel)

    def render(parent: Element = document.body) {

        new ListItem(List(), "divider").render(parent)
        new ListItem(List(vertex)).render(parent)
        new ListItem(List(edges)).render(parent)
        new ListItem(List(text)).render(parent)

        vertexSettings.render(document.body)
        edgesSettings.render(document.body)
        textSettings.render(document.body)
    }

    vertexSettings.settingsChanged += {
        evt => settingsChanged.trigger(new EventArgs[VisualSetup](this))
        true
    }
    edgesSettings.settingsChanged += {
        evt => settingsChanged.trigger(new EventArgs[VisualSetup](this))
        true
    }
    textSettings.settingsChanged += {
        evt => settingsChanged.trigger(new EventArgs[VisualSetup](this))
        true
    }

    vertex.clicked += {
        event =>
            vertexSettings.show()
            false
    }

    edges.clicked += {
        event =>
            edgesSettings.show()
            false
    }

    text.clicked += {
        event =>
            textSettings.show()
            false
    }

    def getDomElement : Element = vertexSettings.getDomElement
}
