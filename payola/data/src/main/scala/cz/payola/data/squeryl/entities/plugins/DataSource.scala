package cz.payola.data.squeryl.entities.plugins

import scala.collection.immutable
import cz.payola.data.squeryl.entities._
import cz.payola.data.squeryl.entities.plugins.parameters._
import org.squeryl.annotations.Transient
import cz.payola.data.squeryl.SquerylDataContextComponent
import cz.payola.domain.entities.plugins.concrete.DataFetcher

/**
  * This object converts [[cz.payola.common.entities.plugins.DataSource]] to [[cz.payola.data.squeryl.entities.plugins.DataSource]]
  */
object DataSource extends EntityConverter[DataSource]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[DataSource] = {
        entity match {
            case e: DataSource => Some(e)
            case e: cz.payola.domain.entities.plugins.DataSource => {
                val dataFetcher = e.plugin.asInstanceOf[DataFetcher]
                Some(new DataSource(e.id, e.name, e.owner.map(User(_)),
                    dataFetcher,e.parameterValues.map(ParameterValue(_))))
            }
            case _ => None
        }
    }
}

class DataSource(
    override val id: String,
    n: String,
    o: Option[User],
    df: cz.payola.domain.entities.plugins.concrete.DataFetcher,
    paramValues: immutable.Seq[ParameterValue[_]])(implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.plugins.DataSource(n, o, df, paramValues)
    with PersistableEntity  with OptionallyOwnedEntity
{
    var pluginId: String = Option(df).map(_.id).getOrElse(null)

    private lazy val _pluginQuery = context.schema.pluginsDataSources.right(this)

    private lazy val _booleanParameterValuesQuery = context.schema.booleanParameterValuesOfDataSources.left(this)

    private lazy val _floatParameterValuesQuery = context.schema.floatParameterValuesOfDataSources.left(this)

    private lazy val _intParameterValuesQuery = context.schema.intParameterValuesOfDataSources.left(this)

    private lazy val _stringParameterValuesQuery = context.schema.stringParameterValuesOfDataSources.left(this)

    @Transient
    private var _parameterValuesLoaded = false

    @Transient
    // This field represents val _parameterValues in common.PluginInstance - it cannot be overriden because it is
    // immutable
    // (can't be filled via lazy-loading)
    private var _paramValues: immutable.Seq[PluginType#ParameterValueType] = immutable.Seq()

    override def plugin = {
        if (pluginId != null) {
            wrapInTransaction {
                _pluginQuery.head.toPlugin
            }
        }
        else {
            null
        }
    }

    override def parameterValues: collection.immutable.Seq[PluginType#ParameterValueType] = {
        if (!_parameterValuesLoaded) {
            wrapInTransaction {
                _paramValues = List(
                    _booleanParameterValuesQuery.toList,
                    _floatParameterValuesQuery.toList,
                    _intParameterValuesQuery.toList,
                    _stringParameterValuesQuery.toList
                ).flatten
            }

            _parameterValuesLoaded = true
        }

        _paramValues
    }

    def associateParameterValues() {
        paramValues.map {
            case paramValue: BooleanParameterValue => context.schema.associate(paramValue, _booleanParameterValuesQuery)
            case paramValue: FloatParameterValue => context.schema.associate(paramValue, _floatParameterValuesQuery)
            case paramValue: IntParameterValue => context.schema.associate(paramValue, _intParameterValuesQuery)
            case paramValue: StringParameterValue => context.schema.associate(paramValue, _stringParameterValuesQuery)
        }
    }
}
