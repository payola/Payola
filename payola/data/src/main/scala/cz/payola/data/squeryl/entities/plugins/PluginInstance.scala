package cz.payola.data.squeryl.entities.plugins

import scala.collection.immutable
import cz.payola.data.squeryl.entities._
import cz.payola.data.squeryl._

/**
 * This object converts [[cz.payola.common.entities.plugins.PluginInstance]] to [[cz.payola.data.squeryl.entities
 * .plugins.PluginInstance]]
 */
object PluginInstance extends EntityConverter[PluginInstance]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[PluginInstance] = {
        entity match {
            case e: PluginInstance => Some(e)
            case e: cz.payola.common.entities.plugins.PluginInstance => {
                val plugin = e.plugin.asInstanceOf[cz.payola.domain.entities.Plugin]
                Some(new PluginInstance(e.id, plugin, e.parameterValues.map(ParameterValue(_)), e.description,
                    e.isEditable))
            }
            case _ => None
        }
    }
}

/**
 * Provides database persistence to [[cz.payola.domain.entities.plugins.PluginInstance]] entities.
 * @param id ID of the plugin instance
 * @param p Plugin the instance is derived from
 * @param paramValues List of parameter values for the plugin instance
 * @param _desc Description
 * @param _isEdit Whether the plugin instance is editable or not
 * @param context Implicit context
 */
class PluginInstance(
    override val id: String,
    p: cz.payola.domain.entities.Plugin,
    paramValues: immutable.Seq[ParameterValue[_]],
    var _desc: String,
    var _isEdit: Boolean)(implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.plugins.PluginInstance(p, paramValues)
    with Entity with DescribedEntity with PluginInstanceLike
{
    var pluginId: String = Option(p).map(_.id).getOrElse(null)

    var analysisId: String = null
}
