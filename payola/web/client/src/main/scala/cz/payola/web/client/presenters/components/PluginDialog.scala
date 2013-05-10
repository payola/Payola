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
    val createDataCubePluginClicked = new SimpleUnitEvent[PluginDialog]
    val createAnalysisPluginClicked = new SimpleUnitEvent[PluginDialog]

    private val pluginListItems = plugins.map { plugin =>
        val anchor = new Anchor(List(new Text(plugin.name)))
        anchor.mouseClicked += { e =>
            pluginNameClicked.triggerDirectly(plugin)
            false
        }
        new ListItem(List(anchor))
    }


    private val createDcvPluginAnchor = new Anchor(List(new Text("create")))

    createDcvPluginAnchor.mouseClicked += { e =>
        createDataCubePluginClicked.triggerDirectly(this)
        false
    }

    private val createAnalysisPluginAnchor = new Anchor(List(new Text("create")))

    createAnalysisPluginAnchor.mouseClicked += { e =>
        createAnalysisPluginClicked.triggerDirectly(this)
        false
    }

    private val createDcvPlugin = new Paragraph(List(new Text("... or "),createDcvPluginAnchor,new Text(" a new DataCube Vocabulary plugin")))
    private val createAnalysisPlugin = new Paragraph(List(new Text("... or "),createAnalysisPluginAnchor,new Text(" a new plugin from an existing analysis")))

    override val body = {
        if (plugins.exists(_.inputCount > 0)){
            List(new UnorderedList(pluginListItems), new Div(List(createDcvPlugin)))
        }else{
            List(new UnorderedList(pluginListItems), new Div(List(createAnalysisPlugin)))
        }
    }
}
