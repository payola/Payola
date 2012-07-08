package cz.payola.data.squeryl.entities

import cz.payola.data._
import org.squeryl.annotations.Transient
import cz.payola.data.squeryl.entities.plugins._
import cz.payola.data.squeryl.entities.plugins.parameters._
import cz.payola.domain.entities.Plugin
import cz.payola.data.squeryl.SquerylDataContextComponent

/**
  * This objects converts [[cz.payola.domain.entities.Plugin]] to [[cz.payola.data.squeryl.entities.PluginDbRepresentation]]
  */
object PluginDbRepresentation extends EntityConverter[PluginDbRepresentation]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[PluginDbRepresentation] = {
        entity match {
            case p: PluginDbRepresentation => Some(p)
            case p: cz.payola.domain.entities.Plugin 
                    => Some(new PluginDbRepresentation(p.id, p.name, pluginClass(p), p.inputCount, p.owner.map(User(_))))
            case _ => None
        }
    }

    private def pluginClass(p: Plugin): String = {
        p.getClass.toString.replace("class ", "")
    }
}

class PluginDbRepresentation(
    override val id: String,
    val name: String,
    val pluginClass: String,
    val inputCount: Int,
    o: Option[User])
    (implicit val context: SquerylDataContextComponent)
    extends PersistableEntity
{

    private var _owner: Option[User] = None

    var ownerId: Option[String] = o.map(_.id)

    private lazy val _ownerQuery = context.schema.pluginOwnership.right(this)

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
      * @return Returns all associated [[cz.payola.data.squeryl.entities.plugins.PluginInstance]]s.
      */
    def pluginInstances: Seq[PluginInstance] = {
        evaluateCollection(_pluginInstancesQuery)
    }

    /**
      * @return Returns all associated [[cz.payola.data.squeryl.entities.plugins.plugins.DataSource]]s.
      */
    def dataSources: Seq[DataSource] = {
        evaluateCollection(_dataSourcesQuery)
    }

    def owner: Option[User] = {
        if (_owner == None){
            if (ownerId != null && ownerId.isDefined) {
                _owner = evaluateCollection(_ownerQuery).headOption
            }
        }

        _owner
    }

    /**
      *
      * @return Returns list of associated [[cz.payola.data.squeryl.entities.plugins.Parameter]]s.
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
      * Associates specified [[cz.payola.data.squeryl.entities.plugins.Parameter]] to plugin.
      *
      * @param parameter - parameter to be associated to represented plugin
      */
    def associateParameter(parameter: Parameter[_]) {
        parameter match {
            case b: BooleanParameter => associate(b, _booleanParameters)
            case f: FloatParameter => associate(f, _floatParameters)
            case i: IntParameter => associate(i, _intParameters)
            case s: StringParameter => associate(s, _stringParameters)
        }
    }

    /**
      * Associates [[cz.payola.data.squeryl.entities.plugins.PluginInstance]] to plugin
      *
      * @param i - plugin instance to bo associated to represented plugin
      */
    def associatePluginInstance(i: cz.payola.data.squeryl.entities.plugins.PluginInstance) {
        associate(i, _pluginInstancesQuery)
    }

    /**
      * Associates [[cz.payola.data.squeryl.entities.plugins.plugins.DataSource]] to plugin
      *
      * @param ds - data source to be associated to represented plugin
      */
    def associateDataSource(ds: cz.payola.data.squeryl.entities.plugins.DataSource) {
        associate(ds, _dataSourcesQuery)
    }

    /**
      * Represented plugin is instantiated.
      *
      * @return Returns represented plugin.
      */
    def createPlugin(): Plugin = {
        // Return properly instantiated Plugin
        instantiate(pluginClass, name, new java.lang.Integer(inputCount), parameters, id)
    }

    private def instantiate(className: String, args: AnyRef*): Plugin = {
        val clazz = java.lang.Class.forName(className)
        val constructor = clazz.getConstructors().find(_.getParameterTypes().size == 4).get

        constructor.newInstance(args: _*).asInstanceOf[Plugin]
    }
}
