package cz.payola.data.squeryl.entities

import cz.payola.domain.PluginCompilerComponent
import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.plugins.concrete.data.PayolaStorage
import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities.plugins._

object PluginDbRepresentation extends EntityConverter[PluginDbRepresentation]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[PluginDbRepresentation] = {
        entity match {
            case p: PluginDbRepresentation => Some(p)
            case p: Plugin => {
                Some(new PluginDbRepresentation(
                    p.id,
                    p.name,
                    p.getClass.getName,
                    p.inputCount,
                    p.owner.map(User(_)),
                    p.isPublic)
                )
            }
            case _ => None
        }
    }
}

/**
 * Provides persistence to [[cz.payola.domain.entities.Plugin]] entities
 * @param id ID of the plugins
 * @param _name Name of the plugin
 * @param pluginClassName Class name of the plugin
 * @param inputCount Input count of the plugin
 * @param o Owner of the plugin
 * @param _isPub Whether plugin is public or not
 * @param context Implicit context
 */
class PluginDbRepresentation(
    override val id: String,
    protected var _name: String,
    val pluginClassName: String,
    val inputCount: Int,
    o: Option[User],
    var _isPub: Boolean)
    (implicit val context: SquerylDataContextComponent)
    extends Entity with ShareableEntity
{
    val ownerId: Option[String] = o.map(_.id)

    var owner: Option[User] = None

    var parameters: Seq[Parameter[_]] = Seq()

    override def classNameText = "plugin database representation"

    /**
     * Represented plugin is instantiated.
     *
     * @return Returns represented plugin.
     */
    def toPlugin: Plugin = {
        val pluginClass = context.asInstanceOf[PluginCompilerComponent].pluginClassLoader.loadClass(pluginClassName)

        // Variables dependent on plugin type.
        val pluginDependsOnContext = pluginClass == classOf[PayolaStorage]
        val argumentCount = if (pluginDependsOnContext) 5 else 4
        val additionalArguments = if (pluginDependsOnContext) List(context) else Nil

        // Find the proper constructor.
        val constructor = pluginClass.getConstructors.find(_.getParameterTypes.size == argumentCount).get
        val constructorArguments = List(name, new java.lang.Integer(inputCount), parameters, id) ++ additionalArguments

        // Instantiate the plugin.
        val instance = constructor.newInstance(constructorArguments: _*)
        val plugin = instance.asInstanceOf[Plugin]

        plugin.owner = owner
        plugin.isPublic = isPublic

        plugin
    }
}
