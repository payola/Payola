package cz.payola.web.client.presenters.components

import cz.payola.common.entities.Plugin
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Modal
import cz.payola.web.client.events.SimpleUnitEvent
import cz.payola.web.client.views.elements.Anchor
import cz.payola.web.client.views.elements.lists._

class PluginDialog(plugins: Seq[Plugin]) extends Modal("Choose a type of plugin", Nil, None)
{
    val pluginNameClicked = new SimpleUnitEvent[Plugin]

    private val pluginListItems = plugins.map { plugin =>
        val anchor = new Anchor(List(new Text(plugin.name)))
        anchor.mouseClicked += { e =>
            pluginNameClicked.triggerDirectly(plugin)
            false
        }
        new ListItem(List(anchor))
    }

    override val body = List(new UnorderedList(pluginListItems))
}
