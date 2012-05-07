package cz.payola.data.entities

import schema.PayolaDB

class Plugin(
        id: String,
        name: String)
    extends cz.payola.domain.entities.Plugin(id, name)
    with PersistableEntity
{
    private lazy val _pluginInstancesQuery =  PayolaDB.pluginsPluginInstances.left(this)

    private lazy val _booleanParameters = PayolaDB.booleanParametersOfPlugins.left(this)

    private lazy val _floatParameters = PayolaDB.floatParametersOfPlugins.left(this)

    private lazy val _intParameters = PayolaDB.intParametersOfPlugins.left(this)

    private lazy val _stringParameters = PayolaDB.stringParametersOfPlugins.left(this)

    def pluginInstances: collection.Seq[PluginInstance] = {
        evaluateCollection(_pluginInstancesQuery)
    }

    override def parameters: collection.Seq[ParameterType] = {
        List(
            evaluateCollection(_booleanParameters),
            evaluateCollection(_floatParameters),
            evaluateCollection(_intParameters),
            evaluateCollection(_stringParameters)
        ).flatten.toSeq
    }

    override def addParameter(p: cz.payola.domain.entities.parameters.Parameter[_]) {
        super.addParameter(p)
        
        if (p.isInstanceOf[BooleanParameter]) {
            associate(p.asInstanceOf[BooleanParameter], _booleanParameters)
        }
        else if (p.isInstanceOf[FloatParameter]) {
            associate(p.asInstanceOf[FloatParameter], _floatParameters)
        }
        else if (p.isInstanceOf[IntParameter]) {
            associate(p.asInstanceOf[IntParameter], _intParameters)
        }
        else if (p.isInstanceOf[StringParameter]) {
            associate(p.asInstanceOf[StringParameter], _stringParameters)
        }
    }
}
