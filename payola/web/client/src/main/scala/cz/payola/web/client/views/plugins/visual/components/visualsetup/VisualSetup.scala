package cz.payola.web.client.views.plugins.visual.components.visualsetup

import cz.payola.web.client.mvvm_api.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.{Element}
import cz.payola.web.client.views.plugins.Plugin
import cz.payola.web.client.views.plugins.visual.{EdgeSettingsModel, TextSettingsModel, VertexSettingsModel}
import cz.payola.web.client.mvvm_api.element.{Anchor, Li, Text}
import cz.payola.web.client.events._

/**
 *
 * @author jirihelmich
 * @created 5/4/12 7:16 PM
 * @package cz.payola.web.client.views.plugins.visual.components.visualsetup
 */

class VisualSetup(var vertexModel: VertexSettingsModel, var edgesModel: EdgeSettingsModel, var textModel: TextSettingsModel) extends Component
{
    val settingsChanged = new ComponentEvent[VisualSetup, EventArgs[VisualSetup]]

    val vertex = new Anchor(List(new Text("Vertices style")), "#")
    val edges = new Anchor(List(new Text("Edges style")), "#")
    val text = new Anchor(List(new Text("Text style")), "#")
    val vertexSettings = new VertexModal(vertexModel)
    val edgesSettings = new EdgeModal(edgesModel)
    val textSettings = new TextModal(textModel)

    def render(parent: Element = document.body) = {

        new Li(List(), "divider").render(parent)
        new Li(List(vertex)).render(parent)
        new Li(List(edges)).render(parent)
        new Li(List(text)).render(parent)

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
            vertexSettings.show
            false
    }

    edges.clicked += {
        event =>
            edgesSettings.show
            false
    }

    text.clicked += {
        event =>
            textSettings.show
            false
    }

    private def constraintSize(size: Int, min: Int, max: Int, default: Int): Int = {
        if (min <= size && size <= max) {
            size
        } else {
            default
        }
    }
}
