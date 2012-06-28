package cz.payola.data.entities.plugins

import cz.payola.data.PayolaDB
import cz.payola.data.entities.plugins.parameters._
import scala.collection.immutable
import org.squeryl.annotations.Transient
import cz.payola.data.entities._

/**
  * This object converts [[cz.payola.common.entities.plugins.PluginInstance]] to [[cz.payola.data.entities.plugins.PluginInstance]]
  */
object PluginInstance
{
    def apply(p: cz.payola.common.entities.plugins.PluginInstance): PluginInstance = {
        p match {
            case instance: PluginInstance => instance
            case _ => {
                val paramValues = p.parameterValues.map(ParameterValue(_))

                val pluginDb = PluginDbRepresentation(p.plugin.asInstanceOf[cz.payola.domain.entities.Plugin])

                val instance = new PluginInstance(p.id, pluginDb.createPlugin(), paramValues, p.description)

                // Create relation between plugin and this instance
                pluginDb.registerPluginInstance(instance)

                // Return converted PluginInstance
                instance
            }
        }
    }
}

class PluginInstance(
    override val id: String,
    p: cz.payola.domain.entities.Plugin,
    paramValues: immutable.Seq[ParameterValue[_]],
    description: String)
    extends cz.payola.domain.entities.plugins.PluginInstance(p, paramValues)
    with PersistableEntity
{
    var pluginId: Option[String] = if (plugin == null) None else Some(plugin.id)

    var analysisId: Option[String] = None

    private lazy val _pluginQuery = PayolaDB.pluginsPluginInstances.right(this)

    private lazy val _booleanParameterValues = PayolaDB.booleanParameterValuesOfPluginInstances.left(this)

    private lazy val _floatParameterValues = PayolaDB.floatParameterValuesOfPluginInstances.left(this)

    private lazy val _intParameterValues = PayolaDB.intParameterValuesOfPluginInstances.left(this)

    private lazy val _stringParameterValues = PayolaDB.stringParameterValuesOfPluginInstances.left(this)

    @Transient
    private var _parameterValuesLoaded = false

    @Transient
    // This field represents val _parameterValues in common.PluginInstance - it cannot be overriden because it is
    // immutable
    // (can't be filled via lazy-loading)
    private var _paramValues: immutable.Seq[PluginType#ParameterValueType] = immutable.Seq()

    override def plugin = {
        if (pluginId != null) {
            evaluateCollection(_pluginQuery)(0).createPlugin()
        }
        else {
            null
        }
    }

    override def parameterValues: collection.immutable.Seq[PluginType#ParameterValueType] = {
        if (!_parameterValuesLoaded) {
            _paramValues = List(
                evaluateCollection(_booleanParameterValues),
                evaluateCollection(_floatParameterValues),
                evaluateCollection(_intParameterValues),
                evaluateCollection(_stringParameterValues)
            ).flatten.toSeq

            _parameterValuesLoaded = true
        }

        _paramValues
    }

    /**
      * This method associated all related [[cz.payola.data.entities.plugins.ParameterValue]]s.
      */
    def associateParameterValues() {
        paramValues.map {
            case paramValue: BooleanParameterValue => associate(paramValue, _booleanParameterValues)
            case paramValue: FloatParameterValue => associate(paramValue, _floatParameterValues)
            case paramValue: IntParameterValue => associate(paramValue, _intParameterValues)
            case paramValue: StringParameterValue => associate(paramValue, _stringParameterValues)
        }
    }
}
