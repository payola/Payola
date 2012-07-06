package cz.payola.data.entities

import cz.payola.data._
import org.squeryl.annotations.Transient
import cz.payola.data.entities.plugins._
import cz.payola.data.entities.plugins.parameters._
import cz.payola.domain.entities.Plugin

/**
  * This objects converts [[cz.payola.domain.entities.Plugin]] to [[cz.payola.data.entities.PluginDbRepresentation]]
  */
object PluginDbRepresentation
{
    def apply(p: Plugin)(implicit context: SquerylDataContextComponent): PluginDbRepresentation = {
        new PluginDbRepresentation(p.id, p.name, pluginClass(p), p.inputCount)
    }

    private def pluginClass(p: Plugin): String = {
        p.getClass.toString.replace("class ", "")
    }
}

class PluginDbRepresentation(override val id: String, val name: String, val pluginClass: String, val inputCount: Int)
    (implicit val context: SquerylDataContextComponent)
    extends PersistableEntity
{
    @Transient
    private var _parametersLoaded = false

    private var params: Seq[Parameter[_]] = Seq()

    private lazy val _pluginInstancesQuery = context.schema.pluginsPluginInstances.left(this)

    private lazy val _dataSourcesQuery = context.schema.pluginsDataSources.left(this)

    private lazy val _booleanParameters = context.schema.booleanParametersOfPlugins.left(this)

    private lazy val _floatParameters = context.schema.floatParametersOfPlugins.left(this)

    private lazy val _intParameters = context.schema.intParametersOfPlugins.left(this)

    private lazy val _stringParameters = context.schema.stringParametersOfPlugins.left(this)

    /**
      * @return Returns all associated [[cz.payola.data.entities.plugins.PluginInstance]]s.
      */
    def pluginInstances: Seq[PluginInstance] = {
        evaluateCollection(_pluginInstancesQuery)
    }

    /**
      * @return Returns all assicated [[cz.payola.data.entities.plugins.plugins.DataSource]]s.
      */
    def dataSources: Seq[DataSource] = {
        evaluateCollection(_dataSourcesQuery)
    }

    /**
      *
      * @return Rerurns list of assicated [[cz.payola.data.entities.plugins.Parameter]]s.
      */
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

    /**
      * Associates specified [[cz.payola.data.entities.plugins.Parameter]] to represented plugin.
      *
      * @param parameter - parameter to be associated to represented plugin
      */
    def addParameter(parameter: Parameter[_]) {
        parameter match {
            case b: BooleanParameter => associate(b, _booleanParameters)
            case f: FloatParameter => associate(f, _floatParameters)
            case i: IntParameter => associate(i, _intParameters)
            case s: StringParameter => associate(s, _stringParameters)
        }
    }

    /**
      * Represented plugin is instantiated.
      *
      * @return Returns reperesented plugin.
      */
    def createPlugin(): Plugin = {
        // Return properly instantiated Plugin
        instantiate(pluginClass, name, new java.lang.Integer(inputCount), parameters, id)
    }

    /**
      * If specifeid [[cz.payola.data.entities.plugins.PluginInstance]] is instance of represented plugin,
      * relation between them is created by this method.
      *
      * @param i - plugin instance to associate to represented plugin
      */
    def registerPluginInstance(i: cz.payola.data.entities.plugins.PluginInstance) {
        associate(i, _pluginInstancesQuery)
    }

    /**
      * If specifeid [[cz.payola.data.entities.plugins.plugins.DataSource]] should be related to represented plugin,
      * relation between them is created by this method.
      *
      * @param ds - data source to associate to represented plugin
      */
    def registerDataSource(ds: cz.payola.data.entities.plugins.DataSource) {
        associate(ds, _dataSourcesQuery)
    }

    private def instantiate(className: String, args: AnyRef*): Plugin = {
        val clazz = java.lang.Class.forName(className)
        val constructor = clazz.getConstructors().find(_.getParameterTypes().size == 4).get

        constructor.newInstance(args: _*).asInstanceOf[Plugin]
    }
}
