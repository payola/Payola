package cz.payola.data.squeryl.entities

import cz.payola.data._
import org.squeryl.annotations.Transient
import cz.payola.data.squeryl.entities.plugins._
import cz.payola.data.squeryl.entities.plugins.parameters._
import cz.payola.domain.entities.Plugin
import cz.payola.data.squeryl.SquerylDataContextComponent
import cz.payola.domain.entities.plugins.concrete.data.PayolaStorage

object PluginDbRepresentation extends EntityConverter[PluginDbRepresentation]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[PluginDbRepresentation] = {
        entity match {
            case p: PluginDbRepresentation => Some(p)
            case p: Plugin => {
                val pluginClass = p.getClass.getName
                Some(new PluginDbRepresentation(p.id, p.name, pluginClass, p.inputCount, p.owner.map(User(_)),
                    p.isPublic))
            }
            case _ => None
        }
    }
}

class PluginDbRepresentation(
    override val id: String,
    val name: String,
    val className: String,
    val inputCount: Int,
    o: Option[User],
    var isPublic: Boolean)
    (implicit val context: SquerylDataContextComponent)
    extends PersistableEntity
{
    val ownerId: Option[String] = o.map(_.id)

    var owner: Option[User] = None
    
    var parameters: Seq[Parameter[_]] = Seq()

    private lazy val _pluginInstancesQuery = context.schema.pluginsPluginInstances.left(this)

    private lazy val _dataSourcesQuery = context.schema.pluginsDataSources.left(this)

    private lazy val _booleanParameters = context.schema.booleanParametersOfPlugins.left(this)

    private lazy val _floatParameters = context.schema.floatParametersOfPlugins.left(this)

    private lazy val _intParameters = context.schema.intParametersOfPlugins.left(this)

    private lazy val _stringParameters = context.schema.stringParametersOfPlugins.left(this)


    /**
      * Associates specified [[cz.payola.data.squeryl.entities.plugins.Parameter]] to plugin.
      *
      * @param parameter - parameter to be associated to represented plugin
      */
    def associateParameter(parameter: Parameter[_]) {
        parameter match {
            case b: BooleanParameter => context.schema.associate(b, _booleanParameters)
            case f: FloatParameter => context.schema.associate(f, _floatParameters)
            case i: IntParameter => context.schema.associate(i, _intParameters)
            case s: StringParameter => context.schema.associate(s, _stringParameters)
        }
    }

    /**
      * Associates [[cz.payola.data.squeryl.entities.plugins.PluginInstance]] to plugin
      *
      * @param i - plugin instance to bo associated to represented plugin
      */
    def associatePluginInstance(i: cz.payola.data.squeryl.entities.plugins.PluginInstance) {
        context.schema.associate(i, _pluginInstancesQuery)
    }

    /**
      * Associates [[cz.payola.data.squeryl.entities.plugins.plugins.DataSource]] to plugin
      *
      * @param ds - data source to be associated to represented plugin
      */
    def associateDataSource(ds: cz.payola.data.squeryl.entities.plugins.DataSource) {
        context.schema.associate(ds, _dataSourcesQuery)
    }

    /**
      * Represented plugin is instantiated.
      *
      * @return Returns represented plugin.
      */
    def toPlugin: Plugin = {
        val pluginClass = Class.forName(className)

        // Variables dependent on plugin type.
        val pluginDependsOnContext = pluginClass == classOf[PayolaStorage]
        val argumentCount = if (pluginDependsOnContext) 5 else 4
        val additionalArguments = if (pluginDependsOnContext) List(context) else Nil

        // Find the proper constructor.
        val constructor = pluginClass.getConstructors.find(_.getParameterTypes().size == argumentCount).get
        val constructorArguments = List(name, new java.lang.Integer(inputCount), parameters, id) ++ additionalArguments

        // Instantiate the plugin.
        val instance = constructor.newInstance(constructorArguments: _*)
        val plugin = instance.asInstanceOf[Plugin]
        plugin.owner = owner
        plugin
    }
}
