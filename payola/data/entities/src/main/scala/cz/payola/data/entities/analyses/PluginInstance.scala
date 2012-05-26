package cz.payola.data.entities.analyses

import cz.payola.data.entities.{PayolaDB, PersistableEntity}
import scala.collection.immutable
import cz.payola.data.entities.analyses.parameters._

object PluginInstance {

    def apply(p: cz.payola.common.entities.analyses.PluginInstance): PluginInstance = {
        val paramValues = p.parameterValues.map(
            _ match {
                case b: BooleanParameterValue => b
                case f: FloatParameterValue => f
                case i: IntParameterValue => i
                case s: StringParameterValue => s
                case b: cz.payola.domain.entities.analyses.parameters.BooleanParameterValue => {
                        val parameter = BooleanParameter(b.parameter.asInstanceOf[cz.payola.domain.entities.analyses.parameters.BooleanParameter])
                        new BooleanParameterValue(b.id, parameter, b.value)
                    }
                case f: cz.payola.domain.entities.analyses.parameters.FloatParameterValue => {
                        val parameter = FloatParameter(f.parameter.asInstanceOf[cz.payola.domain.entities.analyses.parameters.FloatParameter])
                        new FloatParameterValue(f.id, parameter, f.value)
                    }
                case i: cz.payola.domain.entities.analyses.parameters.IntParameterValue => {
                        val parameter = IntParameter(i.parameter.asInstanceOf[cz.payola.domain.entities.analyses.parameters.IntParameter])
                        new IntParameterValue(i.id, parameter, i.value)
                }
                case s: cz.payola.domain.entities.analyses.parameters.StringParameterValue => {
                        val parameter = StringParameter(s.parameter.asInstanceOf[cz.payola.domain.entities.analyses.parameters.StringParameter])
                        new StringParameterValue(s.id, parameter, s.value)
                }
            }
        )

        new PluginInstance(p.id, PluginDbRepresentation(p.plugin).createPlugin(), paramValues, p.description)
    }
}

class PluginInstance(
    override val id: String,
    plugin: cz.payola.domain.entities.analyses.Plugin,
    paramValues: immutable.Seq[ParameterValue[_]],
    description : String)
    extends cz.payola.domain.entities.analyses.PluginInstance(plugin, paramValues)
    with PersistableEntity
{
    val pluginId: Option[String] = if (plugin == null) None else Some(plugin.id)

    var analysisId: Option[String] = None

    private lazy val _booleanParameterValues = PayolaDB.booleanParameterValuesOfPluginInstances.left(this)

    private lazy val _floatParameterValues = PayolaDB.floatParameterValuesOfPluginInstances.left(this)

    private lazy val _intParameterValues = PayolaDB.intParameterValuesOfPluginInstances.left(this)

    private lazy val _stringParameterValues = PayolaDB.stringParameterValuesOfPluginInstances.left(this)

    // Assosiate parameter values to plugin instance
    associateParameterValues

    override def parameterValues: collection.immutable.Seq[PluginType#ParameterValueType] = {
        List(
            evaluateCollection(_booleanParameterValues),
            evaluateCollection(_floatParameterValues),
            evaluateCollection(_intParameterValues),
            evaluateCollection(_stringParameterValues)
        ).flatten.toSeq
    }

    private def associateParameterValues() {
        paramValues.map {
            case paramValue: BooleanParameterValue => associate(paramValue, _booleanParameterValues)
            case paramValue: FloatParameterValue => associate(paramValue, _floatParameterValues)
            case paramValue: IntParameterValue => associate(paramValue, _intParameterValues)
            case paramValue: StringParameterValue => associate(paramValue, _stringParameterValues)
        }
    }
}
