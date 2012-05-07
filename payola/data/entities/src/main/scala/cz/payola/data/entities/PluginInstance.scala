package cz.payola.data.entities

import schema.PayolaDB

class PluginInstance(
        id: String,
        plugin: Plugin,
        analysis: Analysis)
    extends cz.payola.domain.entities.PluginInstance(id, plugin)
    with PersistableEntity
{
    val pluginId: String = if (plugin == null) "" else plugin.id

    val analysisId: String = if (analysis == null) "" else analysis.id

    private lazy val _analysesQuery =  PayolaDB.analysesPluginInstances.right(this)

    private lazy val _booleanParameterInstances = PayolaDB.booleanParameterInstancesOfPluginInstances.left(this)

    private lazy val _floatParameterInstances = PayolaDB.floatParameterInstancesOfPluginInstances.left(this)

    private lazy val _intParameterInstances = PayolaDB.intParameterInstancesOfPluginInstances.left(this)

    private lazy val _stringParameterInstances = PayolaDB.stringParameterInstancesOfPluginInstances.left(this)

    def analyses : collection.Seq[Analysis] = {
        evaluateCollection(_analysesQuery)
    }

    override def parameterInstances: collection.Seq[ParameterInstanceType] = {
        List(
            evaluateCollection(_booleanParameterInstances),
            evaluateCollection(_floatParameterInstances),
            evaluateCollection(_intParameterInstances),
            evaluateCollection(_stringParameterInstances)
        ).flatten.toSeq
    }

    override def addParameterInstance(p: cz.payola.domain.entities.parameters.ParameterInstance[_]) {
        super.addParameterInstance(p)

        if (p.isInstanceOf[BooleanParameterInstance]) {
            associate(p.asInstanceOf[BooleanParameterInstance], _booleanParameterInstances)
        }
        else if (p.isInstanceOf[FloatParameterInstance]) {
            associate(p.asInstanceOf[FloatParameterInstance], _floatParameterInstances)
        }
        else if (p.isInstanceOf[IntParameterInstance]) {
            associate(p.asInstanceOf[IntParameterInstance], _intParameterInstances)
        }
        else if (p.isInstanceOf[StringParameterInstance]) {
            associate(p.asInstanceOf[StringParameterInstance], _stringParameterInstances)
        }
    }
}
