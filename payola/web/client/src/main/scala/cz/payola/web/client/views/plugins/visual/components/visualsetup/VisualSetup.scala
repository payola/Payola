package cz.payola.web.client.views.plugins.visual.components.visualsetup

import cz.payola.web.client.mvvm_api.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.{Element}
import cz.payola.web.client.views.plugins.Plugin
import cz.payola.web.client.events.{ChangedEventArgs, ChangedEvent}
import cz.payola.web.client.views.plugins.visual.{EdgeSettingsModel, TextSettingsModel, VertexSettingsModel}
import cz.payola.web.client.mvvm_api.element.{Anchor, Li, Text}

/**
 *
 * @author jirihelmich
 * @created 5/4/12 7:16 PM
 * @package cz.payola.web.client.views.plugins.visual.components.visualsetup
 */

class VisualSetup(var vertexModel: VertexSettingsModel, var edgesModel: EdgeSettingsModel, var textModel: TextSettingsModel) extends Component
{

    def render(parent: Element = document.body) = {

        new Li(List(), "divider").render(parent)

        val vertex = new Anchor(List(new Text("Vertices style")), "#")
        new Li(List(vertex)).render(parent)

        val edges = new Anchor(List(new Text("Edges style")), "#")
        new Li(List(edges)).render(parent)

        val text = new Anchor(List(new Text("Text style")), "#")
        new Li(List(text)).render(parent)

        val vertexSettings = new VertexModal(vertexModel)
        vertexSettings.render(document.body)

        val edgesSettings = new EdgeModal(edgesModel)
        edgesSettings.render(document.body)

        val textSettings = new TextModal(textModel)
        textSettings.render(document.body)

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
    }

    private def constraintSize(size: Int, min: Int, max: Int, default: Int): Int = {
        if (min <= size && size <= max) {
            size
        } else {
            default
        }
    }
}
