package cz.payola.data.entities.analyses

import cz.payola.data.entities.PersistableEntity

object PluginInstanceBinding {

    def apply(p: cz.payola.common.entities.analyses.PluginInstanceBinding): PluginInstanceBinding = {
        p match {
            case b: PluginInstanceBinding => b
            case _ => new PluginInstanceBinding(
                            p.id,
                            PluginInstance(p.sourcePluginInstance),
                            PluginInstance(p.targetPluginInstance),
                            p.targetInputIndex
                        )
        }
    }
}

class PluginInstanceBinding(
    override val id: String,
    sourcePluginInstance: PluginInstance,
    targetPluginInstance: PluginInstance,
    targetInputIndex: Int = 0)
    extends cz.payola.domain.entities.analyses.PluginInstanceBinding(sourcePluginInstance, targetPluginInstance,
        targetInputIndex)
    with PersistableEntity
{
    val sourcePluginInstanceId = if (sourcePluginInstance == null) None else Some(sourcePluginInstance.id)

    val targetPluginInstanceId = if (targetPluginInstance == null) None else Some(targetPluginInstance.id)

    var analysisId: Option[String] = None
}
