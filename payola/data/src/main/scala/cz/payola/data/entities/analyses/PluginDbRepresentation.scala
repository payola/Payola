package cz.payola.data.entities.analyses

import cz.payola.data.entities.analyses.parameters._
import cz.payola.data.entities.PersistableEntity
import cz.payola.data.PayolaDB
import cz.payola.domain.entities.analyses.Plugin

object PluginDbRepresentation {

    def apply(p: cz.payola.common.entities.analyses.Plugin): PluginDbRepresentation = {
        val pluginDb =  new PluginDbRepresentation(p.id, p.name, pluginClass(p), p.inputCount)

        p.parameters.map(
            _ match {
                case b: BooleanParameter => pluginDb.addParameter(b)
                case f: FloatParameter => pluginDb.addParameter(f)
                case i: IntParameter => pluginDb.addParameter(i)
                case s: StringParameter => pluginDb.addParameter(s)
                case b: cz.payola.domain.entities.analyses.parameters.BooleanParameter
                        => pluginDb.addParameter(BooleanParameter(b))
                case i: cz.payola.domain.entities.analyses.parameters.IntParameter
                        => pluginDb.addParameter(IntParameter(i))
                case f: cz.payola.domain.entities.analyses.parameters.FloatParameter
                        => pluginDb.addParameter(FloatParameter(f))
                case s: cz.payola.domain.entities.analyses.parameters.StringParameter
                        => pluginDb.addParameter(StringParameter(s))
            }
        )
        
        pluginDb
    }
    
    private def pluginClass(p: cz.payola.common.entities.analyses.Plugin): String = {
        p.getClass.toString.replace("class ", "")
    }
}

class PluginDbRepresentation(
    override val id: String,
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

    def createPlugin(): Plugin  = {
        // Return as common.Plugin
        instantiate(pluginClass, name, new java.lang.Integer(inputCount), parameters, id)
    }

    private def instantiate(className: String, args: AnyRef*): Plugin = {
        val clazz = java.lang.Class.forName(className)
        val constructor = clazz.getConstructors()(0)

        constructor.newInstance(args:_*).asInstanceOf[Plugin]
    }
}
