package cz.payola.web.client.presenters.components

import cz.payola.web.client.mvvm.element.extensions.Bootstrap._
import cz.payola.web.client.mvvm.Component
import s2js.adapters.js.dom.Element
import s2js.adapters.js.browser.document
import cz.payola.web.client.mvvm.element._
import cz.payola.web.client.events._
import cz.payola.common.entities.analyses.Plugin

class PluginDialog(plugins: Seq[Plugin]) extends Component
{
    val pluginNameClicked = new ClickedEvent[Plugin]

    private val ul = new UnorderedList(List())

    plugins.foreach{ plugin =>
        val anchor = new Anchor(List(new Text(plugin.name)))
        val item = new ListItem(List(anchor))

        anchor.clicked += { evt =>
            pluginNameClicked.trigger(new ClickedEventArgs[Plugin](plugin))
            false
        }

        item.render(ul.ul)
    }

    private val dialog = new Modal("Choose a type of plugin", List(ul))

    def render(parent: Element = document.body) = {
        dialog.render(parent)
    }

    def show() = dialog.show
    def hide() = dialog.hide
}
