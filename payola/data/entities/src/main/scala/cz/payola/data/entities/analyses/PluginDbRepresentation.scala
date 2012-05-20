package cz.payola.data.entities.analyses

import cz.payola.data.entities.analyses.parameters._
import cz.payola.data.entities.{PersistableEntity, PayolaDB}
import scala.collection.immutable
import cz.payola.domain.rdf.Graph
import cz.payola.domain.IDGenerator

class PluginDbRepresentation(
    val id: String,
    val name: String,
    val pluginClass: String,
    val inputCount: Int)
    extends PersistableEntity
{
    private lazy val _pluginInstancesQuery = PayolaDB.pluginsPluginInstances.left(this)

    private lazy val _booleanParameters = PayolaDB.booleanParametersOfPlugins.left(this)

    private lazy val _floatParameters = PayolaDB.floatParametersOfPlugins.left(this)

    private lazy val _intParameters = PayolaDB.intParametersOfPlugins.left(this)

    private lazy val _stringParameters = PayolaDB.stringParametersOfPlugins.left(this)

    def pluginInstances: collection.Seq[PluginInstance] = {
        evaluateCollection(_pluginInstancesQuery)
    }

    def parameters: collection.immutable.Seq[Parameter[_]] = {
        List(
            evaluateCollection(_booleanParameters),
            evaluateCollection(_floatParameters),
            evaluateCollection(_intParameters),
            evaluateCollection(_stringParameters)
        ).flatten.toSeq
    }

    def addParameter(parameter: cz.payola.domain.entities.analyses.Parameter[_]) {
        parameter match {
            case b: BooleanParameter => associate(b, _booleanParameters)
            case f: FloatParameter => associate(f, _floatParameters)
            case i: IntParameter => associate(i, _intParameters)
            case s: StringParameter => associate(s, _stringParameters)
            case b: cz.payola.domain.entities.analyses.parameters.BooleanParameter
                    => associate(new BooleanParameter(b.id, b.name, b.defaultValue), _booleanParameters)
            case f: cz.payola.domain.entities.analyses.parameters.FloatParameter
                    => associate(new FloatParameter(f.id, f.name, f.defaultValue), _floatParameters)
            case i: cz.payola.domain.entities.analyses.parameters.IntParameter
                    => associate(new IntParameter(i.id, i.name, i.defaultValue), _intParameters)
            case s: cz.payola.domain.entities.analyses.parameters.StringParameter
                    => associate(new StringParameter(s.id, s.name, s.defaultValue), _stringParameters)
        }
    }
}
