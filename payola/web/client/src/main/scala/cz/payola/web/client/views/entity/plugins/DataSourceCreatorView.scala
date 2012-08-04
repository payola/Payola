package cz.payola.web.client.views.entity.plugins

import s2js.adapters.js.html
import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.inputs._
import cz.payola.common.entities.Plugin
import s2js.adapters.html.Element

class DataSourceCreatorView(plugins: Seq[Plugin]) extends ComposedView
{
    val name = new TextInputControl("Name", "", "", "")

    val description = new TextInputControl("Description", "", "", "")

    val plugin = new SelectInputControl(
        plugins.map(p => new SelectOption(p.name, p.id)),
        "Plugin",
        "",
        "",
        ""
    )

    val parameterInputsSpace = new Div

    var parameters: Seq[TextInputControl] = Nil

    val createButton = new Button(new Text("Create"))

    plugin.input.changed += { e =>
        plugins.find(_.id == plugin.input.value).foreach(renderPluginParameters(_))
    }

    def createSubViews = List(
        name,
        description,
        plugin,
        parameterInputsSpace,
        createButton
    )

    override def render(parent: html.Element) {
        super.render(parent)
        renderPluginParameters(plugins.head)
    }

    private def renderPluginParameters(plugin: Plugin) {
        parameters.foreach(_.destroy())
        parameters = plugin.parameters.map(p => new TextInputControl(p.name, p.id, p.defaultValue.toString, ""))
        parameters.foreach(_.render(parameterInputsSpace.htmlElement))
    }
}
