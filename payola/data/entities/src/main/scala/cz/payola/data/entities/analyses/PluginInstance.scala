package cz.payola.data.entities.analyses

import cz.payola.data.entities.{PayolaDB, PersistableEntity, Analysis}
import cz.payola.data.entities.analyses.parameters._
import scala.collection.immutable
import cz.payola.domain.IDGenerator

class PluginInstance(
    override val id: String,
    plugin: cz.payola.domain.entities.analyses.Plugin,
    paramValues: immutable.Seq[ParameterValue[_]])
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
    def associateParameterValues() {
        paramValues.map {
            case paramValue: BooleanParameterValue => associate(paramValue, _booleanParameterValues)
            case paramValue: FloatParameterValue => associate(paramValue, _floatParameterValues)
            case paramValue: IntParameterValue => associate(paramValue, _intParameterValues)
            case paramValue: StringParameterValue => associate(paramValue, _stringParameterValues)
        }
    }

    override def parameterValues: collection.immutable.Seq[PluginType#ParameterValueType] = {
        List(
            evaluateCollection(_booleanParameterValues),
            evaluateCollection(_floatParameterValues),
            evaluateCollection(_intParameterValues),
            evaluateCollection(_stringParameterValues)
        ).flatten.toSeq
    }
}
