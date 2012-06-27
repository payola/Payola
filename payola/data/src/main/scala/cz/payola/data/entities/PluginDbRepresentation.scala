package cz.payola.data.entities

import cz.payola.data.PayolaDB
import org.squeryl.annotations.Transient
import cz.payola.data.entities.plugins._
import cz.payola.data.entities.plugins.parameters._
import cz.payola.domain.entities.Plugin

object PluginDbRepresentation
{
    def apply(p: Plugin): PluginDbRepresentation = {
        new PluginDbRepresentation(p.id, p.name, pluginClass(p), p.inputCount)
    }

    private def pluginClass(p: Plugin): String = {
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
    @Transient
    private var _parametersLoaded = false

    private var params: Seq[Parameter[_]] = Seq()

    private lazy val _pluginInstancesQuery = PayolaDB.pluginsPluginInstances.left(this)

    private lazy val _dataSourcesQuery = PayolaDB.pluginsDataSources.left(this)

    private lazy val _booleanParameters = PayolaDB.booleanParametersOfPlugins.left(this)

    private lazy val _floatParameters = PayolaDB.floatParametersOfPlugins.left(this)

    private lazy val _intParameters = PayolaDB.intParametersOfPlugins.left(this)

    private lazy val _stringParameters = PayolaDB.stringParametersOfPlugins.left(this)

    def pluginInstances: Seq[PluginInstance] = {
        evaluateCollection(_pluginInstancesQuery)
    }

    def dataSources: Seq[DataSource] = {
        evaluateCollection(_dataSourcesQuery)
    }

    def parameters: Seq[Parameter[_]] = {
        if (!_parametersLoaded) {
            params = List(
                evaluateCollection(_booleanParameters),
                evaluateCollection(_floatParameters),
                evaluateCollection(_intParameters),
                evaluateCollection(_stringParameters)
            ).flatten.toSeq

            _parametersLoaded = true
        }

        params
    }

    def addParameter(parameter: Parameter[_]) {
        parameter match {
            case b: BooleanParameter => associate(b, _booleanParameters)
            case f: FloatParameter => associate(f, _floatParameters)
            case i: IntParameter => associate(i, _intParameters)
            case s: StringParameter => associate(s, _stringParameters)
        }
    }

    def createPlugin(): Plugin = {
        // Return properly instantiated Plugin
        instantiate(pluginClass, name, new java.lang.Integer(inputCount), parameters, id)
    }

    def registerPluginInstance(i: cz.payola.data.entities.plugins.PluginInstance) {
        associate(i, _pluginInstancesQuery)
    }

    def registerDataSource(ds: cz.payola.data.entities.plugins.DataSource) {
        associate(ds, _dataSourcesQuery)
    }

    private def instantiate(className: String, args: AnyRef*): Plugin = {
        val clazz = java.lang.Class.forName(className)
        val constructor = clazz.getConstructors().find(_.getParameterTypes().size == 4).get

        constructor.newInstance(args: _*).asInstanceOf[Plugin]
    }
}
