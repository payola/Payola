package cz.payola.data.entities.analyses

import cz.payola.data.entities.PersistableEntity

class PluginInstanceBinding(
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
