package cz.payola.data.entities.analyses

import cz.payola.data.entities.PersistableEntity
import cz.payola.data.PayolaDB
import org.squeryl.annotations.Transient

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

    @Transient
    private var _sourceLoaded = false
    private var _source: cz.payola.domain.entities.analyses.PluginInstance = null
    private lazy val _sourcesQuery = PayolaDB.bindingsOfSourcePluginInstances.right(this)

    @Transient
    private var _targetLoaded = false
    private var _target: cz.payola.domain.entities.analyses.PluginInstance = null
    private lazy val _targetsQuery = PayolaDB.bindingsOfTargetPluginInstances.right(this)

    override def sourcePluginInstance = {
        try{
            if (!_sourceLoaded) {
                _source = evaluateCollection(_sourcesQuery)(0)

                _sourceLoaded = true
            }

            _source
        }
        catch {
            case e: Exception => println("source error")
            null
        }
    }

    override def targetPluginInstance = {
        try {
            if (!_targetLoaded) {
                _target = evaluateCollection(_targetsQuery)(0)

                _targetLoaded = true
            }

            _target
        }
        catch {
            case e: Exception => println("target error")
            null
        }
    }
}
