package cz.payola.data.entities.plugins

import scala.collection.immutable
import cz.payola.data.entities._
import cz.payola.data.entities.plugins.parameters._
import cz.payola.data.PayolaDB
import org.squeryl.annotations.Transient

/**
  * This object converts [[cz.payola.common.entities.plugins.DataSource]] to [[cz.payola.data.entities.plugins.DataSource]]
  */
object DataSource
{
    def apply(dataSource: cz.payola.common.entities.plugins.DataSource): DataSource = {
        dataSource match {
            case ds: DataSource => ds
            case _ => {
                val owner = if (dataSource.owner.isDefined) Some(User(dataSource.owner.get)) else None
                val paramValues = dataSource.parameterValues.map(ParameterValue(_))

                val pluginDb = PluginDbRepresentation(dataSource.plugin.asInstanceOf[cz.payola.domain.entities.Plugin])
                val dataFetcher = pluginDb.createPlugin()
                    .asInstanceOf[cz.payola.domain.entities.plugins.concrete.DataFetcher]

                val source = new DataSource(dataSource.id, dataSource.name, owner, dataFetcher, paramValues)

                // Create relation between plugin and this DataSource
                pluginDb.registerDataSource(source)

                // Return converted DataSource
                source
            }
        }
    }
}

class DataSource(
    override val id: String,
    n: String,
    o: Option[User],
    df: cz.payola.domain.entities.plugins.concrete.DataFetcher,
    paramValues: immutable.Seq[ParameterValue[_]])
    extends cz.payola.domain.entities.plugins.DataSource(n, o, df, paramValues)
    with PersistableEntity
{
    var pluginId: Option[String] = if (df == null) None else Some(df.id)

    var ownerId: Option[String] = o.map(_.id)

    private lazy val _pluginQuery = PayolaDB.pluginsDataSources.right(this)

    private lazy val _ownerQuery = PayolaDB.dataSourceOwnership.right(this)

    private lazy val _booleanParameterValuesQuery = PayolaDB.booleanParameterValuesOfDataSources.left(this)

    private lazy val _floatParameterValuesQuery = PayolaDB.floatParameterValuesOfDataSources.left(this)

    private lazy val _intParameterValuesQuery = PayolaDB.intParameterValuesOfDataSources.left(this)

    private lazy val _stringParameterValuesQuery = PayolaDB.stringParameterValuesOfDataSources.left(this)

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

    override def owner: Option[UserType] = {
        if (_owner == None) {
            if (ownerId != null && ownerId.isDefined) {
                _owner = evaluateCollection(_ownerQuery).headOption
            }
        }

        _owner
    }

    override def parameterValues: collection.immutable.Seq[PluginType#ParameterValueType] = {
        if (!_parameterValuesLoaded) {
            _paramValues = List(
                evaluateCollection(_booleanParameterValuesQuery),
                evaluateCollection(_floatParameterValuesQuery),
                evaluateCollection(_intParameterValuesQuery),
                evaluateCollection(_stringParameterValuesQuery)
            ).flatten.toSeq

            _parameterValuesLoaded = true
        }

        _paramValues
    }

    def associateParameterValues() {
        paramValues.map {
            case paramValue: BooleanParameterValue => associate(paramValue, _booleanParameterValuesQuery)
            case paramValue: FloatParameterValue => associate(paramValue, _floatParameterValuesQuery)
            case paramValue: IntParameterValue => associate(paramValue, _intParameterValuesQuery)
            case paramValue: StringParameterValue => associate(paramValue, _stringParameterValuesQuery)
        }
    }
}
