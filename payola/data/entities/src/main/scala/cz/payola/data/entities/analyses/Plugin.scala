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

    def evaluate(
        instance: cz.payola.domain.entities.analyses.PluginInstance,
        inputs: IndexedSeq[Graph],
        progressReporter: Plugin#ProgressReporter): Graph = { null }
}
