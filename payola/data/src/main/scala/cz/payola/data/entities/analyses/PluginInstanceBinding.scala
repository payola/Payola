package cz.payola.data.entities.analyses

import cz.payola.data.entities.PersistableEntity
import cz.payola.data.PayolaDB

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
    source: PluginInstance,
    target: PluginInstance,
    private val _targetInputIdx: Int = 0)
    extends cz.payola.domain.entities.analyses.PluginInstanceBinding(source, target, _targetInputIdx)
    with PersistableEntity
{
    val sourcePluginInstanceId = if (source == null) None else Some(source.id)

    val targetPluginInstanceId = if (target == null) None else Some(target.id)

    var analysisId: Option[String] = None

    private lazy val _sourcesQuery = PayolaDB.bindingsOfSourcePluginInstances.right(this)

    private lazy val _targetsQuery = PayolaDB.bindingsOfTargetPluginInstances.right(this)

    override def sourcePluginInstance = {
        try{
            if (sourcePluginInstanceId != null) {
                evaluateCollection(_sourcesQuery)(0)
            }
            else {
                null
            }
        }
        catch {
            case e: Exception => println("source error")
            null
        }
    }

    override def targetPluginInstance = {
        try {
            if (targetPluginInstanceId != null) {
                evaluateCollection(_targetsQuery)(0)
            }
            else {
                null
            }
        }
        catch {
            case e: Exception => println("source error")
            null
        }
    }
}
