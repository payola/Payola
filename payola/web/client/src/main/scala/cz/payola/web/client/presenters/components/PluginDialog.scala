package cz.payola.web.client.presenters.components

import cz.payola.web.client.views.Component
import s2js.adapters.js.dom._
import s2js.adapters.js.browser.document
import cz.payola.common.entities.Plugin
import cz.payola.web.client.views.events._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.extensions.bootstrap.Modal
import cz.payola.web.client.events.SimpleEvent
import cz.payola.web.client.views.elements.Anchor

class PluginDialog(plugins: Seq[Plugin])
{
    val pluginNameClicked = new SimpleEvent[Plugin]

    private val ul = new UnorderedList(List())

    plugins.foreach{ plugin =>
        val anchor = new Anchor(List(new Text(plugin.name)))
        val item = new ListItem(List(anchor))

        anchor.mouseClicked += { e =>
            pluginNameClicked.triggerDirectly(plugin)
            false
        }

        item.render(ul.domElement)
    }

    private val dialog = new Modal("Choose a type of plugin", List(ul), false)

    def render(parent: Node) = {
        dialog.render(parent)
    }

    def show() = dialog.show
    def hide() = dialog.hide

    def domElement: Element = {
        dialog.domElement
    }
}
