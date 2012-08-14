package cz.payola.web.client.views.entity.plugins

import s2js.adapters.html
import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements._
import cz.payola.common.entities.Plugin
import cz.payola.web.client.views.elements.form.fields._
import cz.payola.web.client.views.bootstrap.InputControl

class DataSourceCreatorView(plugins: Seq[Plugin]) extends ComposedView
{
    val name = new InputControl("Name", new TextInput("", "", ""), Some("span2"))

    val description = new InputControl("Description", new TextInput("", "", ""), Some("span2"))

    val plugin = new InputControl(
        "Plugin",
        new Select("", "", "", plugins.map(p => new SelectOption(p.name, p.id))), Some("span2")
    )

    val parameterInputsSpace = new Div

    var parameters: Seq[InputControl[TextInput]] = Nil

    val createButton = new Button(new Text("Create"))

    plugin.field.changed += { e =>
        plugins.find(_.id == plugin.field.value).foreach(renderPluginParameters(_))
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
        parameters = plugin.parameters.map { p =>
            new InputControl(p.name, new TextInput(p.id, p.defaultValue.toString, ""), Some("span2"))
        }
        parameters.foreach(_.render(parameterInputsSpace.htmlElement))
    }
}
