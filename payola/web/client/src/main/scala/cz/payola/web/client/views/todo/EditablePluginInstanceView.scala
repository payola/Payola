package cz.payola.web.client.views.todo

import cz.payola.common.entities.Plugin
import cz.payola.common.entities.plugins.parameters._
import cz.payola.web.client.views.bootstrap.inputs._
import cz.payola.web.client.presenters.models.ParameterValue
import scala.collection.immutable.HashMap
import scala.collection._
import cz.payola.web.client.events.SimpleUnitEvent
import cz.payola.web.client.views.elements._
import cz.payola.web.client.View

class EditablePluginInstanceView(id: String, pluginI: Plugin, predecessors: Seq[PluginInstanceView] = List(),
    defaultValues: Map[String, String] = new HashMap[String, String]()) extends PluginInstanceView(id, pluginI, predecessors, defaultValues)
{
    val connectButtonClicked = new SimpleUnitEvent[PluginInstanceView]

    val deleteButtonClicked = new SimpleUnitEvent[PluginInstanceView]

    val parameterValueChanged = new SimpleUnitEvent[ParameterValue]

    def getAdditionalControlsViews : Seq[View] = {
        val connect = new Button(new Text("Add connection"))
        connect.mouseClicked += { e =>
            connectButtonClicked.triggerDirectly(this)
            false
        }

        val delete = new Button(new Text("Delete"), "btn-danger")
        delete.mouseClicked += { e =>
            deleteButtonClicked.triggerDirectly(this)
            false
        }

        List(connect, delete)
    }

    def getParameterViews = getPlugin.parameters.map { param =>

        val defaultVal = if (defaultValues.isDefinedAt(param.name)) defaultValues(param.name) else param.defaultValue.toString
        val field = param match {
            case p: BooleanParameter => new CheckboxInputControl(param.name, param.id, defaultVal, "Enter parameter value")
            case p: FloatParameter => new NumericInputControl(param.name, param.id, defaultVal, "Enter parameter value")
            case p: IntParameter => new NumericInputControl(param.name, param.id, defaultVal, "Enter parameter value")
            case _ => new TextInputControl(param.name, param.id, defaultVal, "Enter parameter value")
        }

        field.input.changed += { args =>
            parameterValueChanged.triggerDirectly(new ParameterValue(getId, param.id, param.name, field.input.value, field))
            false
        }

        field
    }
}
