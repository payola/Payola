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
import cz.payola.web.client.models.PrefixApplier

class EditablePluginInstanceView(pluginInst: PluginInstance, predecessors: Seq[PluginInstanceView] = List(),
    prefixApplier: PrefixApplier) extends PluginInstanceView(pluginInst, predecessors, prefixApplier)
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

    def getParameterViews : Seq[View] = filterParams(getPlugin.parameters).flatMap { param =>

        pluginInstance.getParameter(param.name).map { v =>
            val field = param match {
                case p: BooleanParameter => new CheckBox(param.id, v.asInstanceOf[Boolean], "Enter parameter value")
                case p: IntParameter => new NumericInput(param.id, v.asInstanceOf[Int], "Enter parameter value")
                case p: StringParameter if p.isPattern => new TextArea(param.id, applyPrefix(p, v.toString), "Enter parameter value")
                case p: StringParameter if p.isMultiline => new TextArea(param.id, applyPrefix(p, v.toString), "Enter parameter value")
                case p: StringParameter => new TextArea(param.id, applyPrefix(p, v.toString), "Enter parameter value")
                case _ => new TextInput(param.id, v.toString, "Enter parameter value")
            }

            val inputControl = new InputControl(parameterName(param), field, None, None)
            inputControl.delayedChanged += { _ => {
                val value = param match {
                    case p: StringParameter if p.canContainUrl => prefixApplier.disapplyPrefix(field.value.toString)
                    case _ => field.value.toString
                }

                parameterValueChanged.triggerDirectly(new ParameterValue(getId, param.id, param.name, value, inputControl))
            }}

            if (!pluginInstance.isEditable) {
                field.setIsEnabled(false)
            }

            inputControl
        }
    }
    def applyPrefix(p: StringParameter, url: String): String = {
        p match {
            case s : StringParameter if s.canContainUrl => prefixApplier.applyPrefix(url)
            case _ => url
        }
    }
}
