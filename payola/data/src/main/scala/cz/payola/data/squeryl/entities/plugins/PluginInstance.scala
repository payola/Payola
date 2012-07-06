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
                val i = new PluginInstance(e.id, plugin, e.parameterValues.map(ParameterValue(_)), e.description)
                // Create relation between plugin and this instance // TODO why there? this should just convert it.
                PluginDbRepresentation(plugin).registerPluginInstance(i)
                Some(i)
            }
            case _ => None
        }
    }
}

class PluginInstance(
    override val id: String,
    p: cz.payola.domain.entities.Plugin,
    paramValues: immutable.Seq[ParameterValue[_]],
    description: String)(implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.plugins.PluginInstance(p, paramValues)
    with PersistableEntity
{
    var pluginId: Option[String] = if (plugin == null) None else Some(plugin.id)

    var analysisId: Option[String] = None

    private lazy val _pluginQuery = context.schema.pluginsPluginInstances.right(this)

    private lazy val _booleanParameterValues = context.schema.booleanParameterValuesOfPluginInstances.left(this)

    private lazy val _floatParameterValues = context.schema.floatParameterValuesOfPluginInstances.left(this)

    private lazy val _intParameterValues = context.schema.intParameterValuesOfPluginInstances.left(this)

    private lazy val _stringParameterValues = context.schema.stringParameterValuesOfPluginInstances.left(this)

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
      * This method associated all related [[cz.payola.data.squeryl.entities.plugins.ParameterValue]]s.
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
