package cz.payola.data.squeryl.entities.analyses

import cz.payola.data.squeryl.entities._
import org.squeryl.annotations.Transient
import cz.payola.data.squeryl.entities.plugins.PluginInstance
import cz.payola.data.squeryl.SquerylDataContextComponent
import cz.payola.domain.entities.User
import scala.Some
import cz.payola.domain.entities.Analysis

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

class PluginInstanceBinding(
    override val id: String,
    s: PluginInstance,
    t: PluginInstance,
    idx: Int = 0)(implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.analyses.PluginInstanceBinding(s, t, idx)
    with PersistableEntity
{
    val sourcePluginInstanceId: String = Option(s).map(_.id).getOrElse(null)

    val targetPluginInstanceId: String = Option(t).map(_.id).getOrElse(null)

    val inputIndex = idx

    var analysisId: String = null

    // TODO: setter
    def setSource(value: PluginInstanceType) {_sourcePluginInstance = value}

    // TODO: setter
    def setTarget(value: PluginInstanceType) {_targetPluginInstance = value}
}
