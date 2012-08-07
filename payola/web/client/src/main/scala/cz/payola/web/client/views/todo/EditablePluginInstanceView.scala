package cz.payola.web.client.views.todo

import cz.payola.common.entities.plugins.parameters._
import cz.payola.web.client.views.bootstrap.inputs._
import cz.payola.web.client.presenters.models.ParameterValue
import scala.collection._
import cz.payola.web.client.events.SimpleUnitEvent
import cz.payola.web.client.views.elements._
import cz.payola.web.client.View
import cz.payola.common.entities.plugins._

class EditablePluginInstanceView(pluginInst: PluginInstance, predecessors: Seq[PluginInstanceView] = List())
    extends PluginInstanceView(pluginInst, predecessors)
{
    val connectButtonClicked = new SimpleUnitEvent[EditablePluginInstanceView]

    val deleteButtonClicked = new SimpleUnitEvent[EditablePluginInstanceView]

    val parameterValueChanged = new SimpleUnitEvent[ParameterValue]

    def getAdditionalControlsViews : Seq[View] = {
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

        val instanceValue = pluginInstance.getParameter(param.name)

        // TODO INPUT
        val defaultVal = instanceValue.getOrElse("").toString
        val field = param match {
            case p: BooleanParameter => new CheckboxInputControl(param.name, param.id, defaultVal, "Enter parameter value")
            case p: FloatParameter => new NumericInputControl(param.name, param.id, defaultVal, "Enter parameter value")
            case p: IntParameter => new NumericInputControl(param.name, param.id, defaultVal, "Enter parameter value")
            case p: StringParameter => if(p.isMultiline){
                new TextAreaInputControl(param.name, param.id, defaultVal, "Enter parameter value")
            }else{
                new TextInputControl(param.name, param.id, defaultVal, "Enter parameter value")
            }
            case _ => new TextInputControl(param.name, param.id, defaultVal, "Enter parameter value")
        }

        field.input.changed += { args =>
            parameterValueChanged.triggerDirectly(new ParameterValue(getId, param.id, param.name, field.input.value, field))
            false
        }

        field
    }
}
