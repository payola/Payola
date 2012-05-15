package cz.payola.data.entities.analyses

import cz.payola.data.entities.analyses.parameters._
import cz.payola.data.entities.{PersistableEntity, PayolaDB}
import scala.collection.immutable
import cz.payola.domain.rdf.Graph

class Plugin(name: String, inputCount: Int, params: immutable.Seq[cz.payola.domain.entities.analyses.Plugin#ParameterType])
    extends cz.payola.domain.entities.analyses.Plugin(name, inputCount, params)
    with PersistableEntity
{
    private lazy val _pluginInstancesQuery = PayolaDB.pluginsPluginInstances.left(this)

    private lazy val _booleanParameters = PayolaDB.booleanParametersOfPlugins.left(this)

    private lazy val _floatParameters = PayolaDB.floatParametersOfPlugins.left(this)

    private lazy val _intParameters = PayolaDB.intParametersOfPlugins.left(this)

    private lazy val _stringParameters = PayolaDB.stringParametersOfPlugins.left(this)

    // Assosiate parameters to plugin
    if (params != null) {
        params.map(
            _ match {
                case param: BooleanParameter => param.pluginId = Some(id)
                case param: FloatParameter => param.pluginId = Some(id)
                case param: IntParameter => param.pluginId = Some(id)
                case param: StringParameter => param.pluginId = Some(id)
            }
        )
    }

    def pluginInstances: collection.Seq[PluginInstance] = {
        evaluateCollection(_pluginInstancesQuery)
    }

    override def parameters: collection.immutable.Seq[ParameterType] = {
        List(
            evaluateCollection(_booleanParameters),
            evaluateCollection(_floatParameters),
            evaluateCollection(_intParameters),
            evaluateCollection(_stringParameters)
        ).flatten.toSeq
    }

    override def createInstance(): cz.payola.domain.entities.analyses.PluginInstance = {
        // Create data.entities plugin instance with data.entities parameter values
        new PluginInstance(
            this,
            parameters.map(
                _ match {
                    case p: cz.payola.data.entities.analyses.parameters.BooleanParameter => p.createValue(None).asInstanceOf[BooleanParameterValue]
                    case p: cz.payola.data.entities.analyses.parameters.FloatParameter => p.createValue(None).asInstanceOf[FloatParameterValue]
                    case p: cz.payola.data.entities.analyses.parameters.IntParameter => p.createValue(None).asInstanceOf[IntParameterValue]
                    case p: cz.payola.data.entities.analyses.parameters.StringParameter => p.createValue(None).asInstanceOf[StringParameterValue]
                    case p: cz.payola.domain.entities.analyses.parameters.BooleanParameter => new BooleanParameter(p.name, p.defaultValue).createValue(None).asInstanceOf[BooleanParameterValue]
                    case p: cz.payola.domain.entities.analyses.parameters.FloatParameter => new FloatParameter(p.name, p.defaultValue).createValue(None).asInstanceOf[FloatParameterValue]
                    case p: cz.payola.domain.entities.analyses.parameters.IntParameter => new IntParameter(p.name, p.defaultValue).createValue(None).asInstanceOf[IntParameterValue]
                    case p: cz.payola.domain.entities.analyses.parameters.StringParameter => new StringParameter(p.name, p.defaultValue).createValue(None).asInstanceOf[StringParameterValue]
                }
            )
        )
    }

    def evaluate(
        instance: cz.payola.domain.entities.analyses.PluginInstance,
        inputs: IndexedSeq[Graph],
        progressReporter: Double => Unit): Graph = { null }
}
