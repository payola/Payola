package cz.payola.web.client.views.entity.plugins

import cz.payola.common.entities.Plugin
import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.bootstrap.InputControl
import cz.payola.web.client.views.elements.form.fields._
import cz.payola.web.client.views.elements._
import s2js.adapters.html
import cz.payola.web.client.View
import cz.payola.web.client.events._
import cz.payola.web.client.presenters.entity.plugins.PluginParameterEventArgs

class DataSourceDetailView(plugins: Seq[Plugin], pluginFieldEditable: Boolean, additionalViews: Seq[View]) extends ComposedView
{

    val nameChanged = new SimpleUnitEvent[InputControl[TextInput]]
    val descriptionChanged = new SimpleUnitEvent[InputControl[TextInput]]

    val parameterValueChanged = new UnitEvent[InputControl[TextInput], PluginParameterEventArgs[InputControl[TextInput]]]

    val name = new InputControl("Name", new TextInput("name", "", ""), None, None)
    name.delayedChanged += {e => nameChanged.triggerDirectly(name)}

    val description = new InputControl("Description", new TextInput("description", "", ""), None, None)
    description.delayedChanged += {e => descriptionChanged.triggerDirectly(description)}

    val plugin = new InputControl(
        "Plugin",
        new Select("", "", "", plugins.map(p => new SelectOption(p.name, p.id)), "form-control"), None, None
    )

    val parameterInputsSpace = new Div

    var parameters: Seq[InputControl[TextInput]] = Nil

    plugin.field.changed += { e =>
        plugins.find(_.id == plugin.field.value).foreach(renderPluginParameters(_))
    }

    if (!pluginFieldEditable){
        plugin.field.disable()
    }

    def createSubViews = List(
        name,
        description,
        plugin,
        parameterInputsSpace
    ) ++ additionalViews

    def getInputFieldForParameterID(parameterID: String) = {
        parameters.find(_.field.name == parameterID)
    }

    def selectDataFetcherWithID(fetcherID: String) {
        plugin.field.updateValue(fetcherID)
        plugins.find(_.id == fetcherID).foreach(renderPluginParameters(_))

    }

    override def render(parent: html.Element) {
        super.render(parent)
        renderPluginParameters(plugins.head)
    }

    private def renderPluginParameters(plugin: Plugin) {
        parameters.foreach(_.destroy())
        parameters = plugin.parameters.map { p =>
            val ic = new InputControl(p.name, new TextInput(p.id, p.defaultValue.toString, ""), None, None)
            ic.delayedChanged += { e =>
                parameterValueChanged.trigger(new PluginParameterEventArgs[InputControl[TextInput]](ic, p))
            }
            ic
        }
        parameters.foreach(_.render(parameterInputsSpace.htmlElement))
    }

}
