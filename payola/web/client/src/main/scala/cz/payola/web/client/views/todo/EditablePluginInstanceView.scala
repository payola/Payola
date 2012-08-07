package cz.payola.web.client.views.todo

import cz.payola.common.entities.Plugin
import cz.payola.common.entities.plugins.parameters._
import cz.payola.web.client.presenters.models.ParameterValue
import scala.collection.immutable.HashMap
import scala.collection._
import cz.payola.web.client.events.SimpleUnitEvent
import cz.payola.web.client.views.elements._
import cz.payola.web.client.View
import cz.payola.web.client.views.elements.form.fields._
import cz.payola.web.client.views.bootstrap.InputControl

class EditablePluginInstanceView(id: String, pluginI: Plugin, predecessors: Seq[PluginInstanceView] = List(),
    defaultValues: Map[String, String] = new HashMap[String, String]())
    extends PluginInstanceView(id, pluginI, predecessors, defaultValues)
{
    val connectButtonClicked = new SimpleUnitEvent[EditablePluginInstanceView]

    val deleteButtonClicked = new SimpleUnitEvent[EditablePluginInstanceView]

    val parameterValueChanged = new SimpleUnitEvent[ParameterValue]

    def getAdditionalControlsViews: Seq[View] = {
        val connect = new Button(new Text("Add Connection"))
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

        val defaultVal = if (defaultValues.isDefinedAt(param.name)) {
            defaultValues(param.name)
        }
        else {
            param.defaultValue.toString
        }

        val field = param match {
            case p: BooleanParameter => new CheckBox(param.id, defaultVal.toBoolean, "Enter parameter value")
            case p: IntParameter => new NumericInput(param.id, defaultVal.toInt, "Enter parameter value")
            case p: StringParameter => {
                if (p.isMultiline) {
                    new TextArea(param.id, defaultVal, "Enter parameter value")
                } else {
                    new TextInput(param.id, defaultVal, "Enter parameter value")
                }
            }
            case _ => new TextInput(param.id, defaultVal, "Enter parameter value")
        }

        val inputControl = new InputControl(param.name, field)
        inputControl.delayedChanged += { _ =>
            parameterValueChanged.triggerDirectly(new ParameterValue(getId, param.id, param.name, field.value.toString,
                inputControl))
        }

        inputControl
    }
}
