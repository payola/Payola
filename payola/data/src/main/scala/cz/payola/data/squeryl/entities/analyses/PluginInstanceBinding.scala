package cz.payola.data.squeryl.entities.analyses

import cz.payola.data.squeryl.entities._
import cz.payola.data.squeryl.entities.plugins.PluginInstance
import cz.payola.data.squeryl._

/**
 * This objects converts [[cz.payola.common.entities.analyses.PluginInstanceBinding]]
 * to [[cz.payola.data.squeryl.entities.analyses.PluginInstanceBinding]]
 */
object PluginInstanceBinding extends EntityConverter[PluginInstanceBinding]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[PluginInstanceBinding] = {
        entity match {
            case e: PluginInstanceBinding => Some(e)
            case e: cz.payola.common.entities.analyses.PluginInstanceBinding => {
                val convertedBinding = new PluginInstanceBinding(e.id, PluginInstance(e.sourcePluginInstance),
                    PluginInstance(e.targetPluginInstance), e.targetInputIndex)
                Some(convertedBinding)
            }
            case _ => None
        }
    }
}

/**
 * Provides database persistence to [[cz.payola.domain.entities.analyses.PluginInstanceBinding]] entities.
 * @param id ID of this binding
 * @param s Source plugin instance of this binding
 * @param t Target plugin instance of this binding
 * @param idx Input index of this binding
 * @param context Implicit context
 */
class PluginInstanceBinding(
    override val id: String,
    s: PluginInstance,
    t: PluginInstance,
    idx: Int = 0)(implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.analyses.PluginInstanceBinding(s, t, idx)
    with Entity
{
    val sourcePluginInstanceId: String = Option(s).map(_.id).getOrElse(null)

    val targetPluginInstanceId: String = Option(t).map(_.id).getOrElse(null)

    val inputIndex = idx

    var analysisId: String = null

    def sourcePluginInstance_=(value: PluginInstanceType) {
        _sourcePluginInstance = value
    }

    def targetPluginInstance_=(value: PluginInstanceType) {
        _targetPluginInstance = value
    }
}
