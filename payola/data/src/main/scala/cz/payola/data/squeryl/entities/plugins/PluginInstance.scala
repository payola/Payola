package cz.payola.data.squeryl.entities.plugins

import cz.payola.data.squeryl.entities.plugins.parameters._
import scala.collection.immutable
import org.squeryl.annotations.Transient
import cz.payola.data.squeryl.entities._
import cz.payola.data.squeryl.SquerylDataContextComponent

/**
  * This object converts [[cz.payola.common.entities.plugins.PluginInstance]] to [[cz.payola.data.squeryl.entities.plugins.PluginInstance]]
  */
object PluginInstance extends EntityConverter[PluginInstance]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[PluginInstance] = {
        entity match {
            case e: PluginInstance => Some(e)
            case e: cz.payola.common.entities.plugins.PluginInstance => {
                val plugin = e.plugin.asInstanceOf[cz.payola.domain.entities.Plugin]
                Some(new PluginInstance(e.id, plugin, e.parameterValues.map(ParameterValue(_)), e.description, e.isEditable))
            }
            case _ => None
        }
    }
}

class PluginInstance(
    override val id: String,
    p: cz.payola.domain.entities.Plugin,
    paramValues: immutable.Seq[ParameterValue[_]],
    var _desc: String,
    var _isEdit: Boolean)(implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.plugins.PluginInstance(p, paramValues)
    with PersistableEntity with DescribedEntity with PluginInstanceLike
{
    var pluginId: String = Option(p).map(_.id).getOrElse(null)

    var analysisId: String = null
}
