package cz.payola.data.squeryl.entities

import cz.payola.data.squeryl.entities.plugins._
import cz.payola.domain.entities.Plugin
import cz.payola.data.squeryl.SquerylDataContextComponent
import cz.payola.domain.entities.plugins.concrete.data.PayolaStorage
import cz.payola.domain._
import scala.Some

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
    var _isPub: Boolean)
    (implicit val context: SquerylDataContextComponent)
    extends Entity(id) with PersistableEntity with ShareableEntity
{
    val ownerId: Option[String] = o.map(_.id)

    var owner: Option[User] = None
    
    var parameters: Seq[Parameter[_]] = Seq()

    def entityTypeName = "plugin database representation"

    /**
      * Represented plugin is instantiated.
      *
      * @return Returns represented plugin.
      */
    def toPlugin: Plugin = {
        val pluginClass = context.asInstanceOf[PluginCompilerComponent].pluginClassLoader.loadClass(className)

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
