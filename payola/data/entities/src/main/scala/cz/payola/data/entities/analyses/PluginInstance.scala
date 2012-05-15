package cz.payola.data.entities.analyses

import cz.payola.data.entities.{PayolaDB, PersistableEntity, Analysis}
import cz.payola.data.entities.analyses.parameters._
import scala.collection.immutable
import cz.payola.domain.rdf.Graph

class PluginInstance(plugin: Plugin, paramValues: immutable.Seq[ParameterValue[_]])
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
    if (paramValues != null) {
        paramValues.map {
            case paramValue: BooleanParameterValue => paramValue.pluginInstanceId = Some(id)
            case paramValue: FloatParameterValue => paramValue.pluginInstanceId = Some(id)
            case paramValue: IntParameterValue => paramValue.pluginInstanceId = Some(id)
            case paramValue: StringParameterValue => paramValue.pluginInstanceId = Some(id)
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
