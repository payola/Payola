package cz.payola.web.client.views.entity.plugins

import cz.payola.common.entities.plugins.parameters._
import cz.payola.web.client.presenters.models.ParameterValue
import scala.collection._
import cz.payola.web.client.events.SimpleUnitEvent
import cz.payola.web.client.views.elements._
import cz.payola.web.client.View
import cz.payola.common.entities.plugins._
import cz.payola.web.client.views.elements.form.fields._
import cz.payola.web.client.views.bootstrap.InputControl

class EditablePluginInstanceView(pluginInst: PluginInstance, predecessors: Seq[PluginInstanceView] = List())
    extends PluginInstanceView(pluginInst, predecessors)
{
    val connectButtonClicked = new SimpleUnitEvent[EditablePluginInstanceView]

    val deleteButtonClicked = new SimpleUnitEvent[EditablePluginInstanceView]

    val parameterValueChanged = new SimpleUnitEvent[ParameterValue]

    alertDiv.addCssClass("editable")

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

    def getParameterViews = getPlugin.parameters.flatMap { param =>
        pluginInstance.getParameter(param.name).map { v =>
            val field = param match {
                case p: BooleanParameter => new CheckBox(param.id, v.asInstanceOf[Boolean], "Enter parameter value")
                case p: IntParameter => new NumericInput(param.id, v.asInstanceOf[Int], "Enter parameter value")
                case p: StringParameter if p.isMultiline => new TextArea(param.id, v.toString, "Enter parameter value")
                case _ => new TextInput(param.id, v.toString, "Enter parameter value")
            }

            val inputControl = new InputControl(param.name, field, None)
            inputControl.delayedChanged += { _ =>
                parameterValueChanged.triggerDirectly(new ParameterValue(getId, param.id, param.name,
                    field.value.toString, inputControl))
            }

            if (!pluginInstance.isEditable) {
                field.setIsEnabled(false)
            }

            inputControl
        }
    }
}
