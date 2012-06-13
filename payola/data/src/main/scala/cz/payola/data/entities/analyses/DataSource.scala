package cz.payola.data.entities.analyses

import scala.collection.immutable
import cz.payola.data.entities._
import cz.payola.data.entities.analyses.parameters._
import cz.payola.data.PayolaDB
import org.squeryl.annotations.Transient

object DataSource {
    def apply(dataSource: cz.payola.common.entities.analyses.DataSource): DataSource =  {
        dataSource match {
            case ds: DataSource => ds
            case _ => {
                val owner = if (dataSource.owner.isDefined) Some(User(dataSource.owner.get)) else None
                val paramValues = dataSource.parameterValues.map(
                    _ match {
                        case b: BooleanParameterValue => b
                        case f: FloatParameterValue => f
                        case i: IntParameterValue => i
                        case s: StringParameterValue => s
                        case b: cz.payola.domain.entities.analyses.parameters.BooleanParameterValue => BooleanParameterValue(b)
                        case f: cz.payola.domain.entities.analyses.parameters.FloatParameterValue => FloatParameterValue(f)
                        case i: cz.payola.domain.entities.analyses.parameters.IntParameterValue => IntParameterValue(i)
                        case s: cz.payola.domain.entities.analyses.parameters.StringParameterValue => StringParameterValue(s)
                    }
                )

                val pluginDb = PluginDbRepresentation(dataSource.plugin)
                val dataFetcher = pluginDb.createPlugin().asInstanceOf[cz.payola.domain.entities.analyses.plugins.DataFetcher]

                val source = new DataSource(dataSource.id, dataSource.name, owner, dataFetcher, paramValues)

                // Create relation between plugin and this DataSource
                pluginDb.registerDataSource(source)

                source
            }
        }
    }
}

class DataSource(
    override val id: String,
    n: String,
    o: Option[User],
    p: cz.payola.domain.entities.analyses.plugins.DataFetcher,
    paramValues: immutable.Seq[ParameterValue[_]])
    extends cz.payola.domain.entities.analyses.DataSource(n, o, p, paramValues)
    with PersistableEntity
{
    var pluginId: Option[String] = if (plugin == null) None else Some(plugin.id)

    var ownerId: Option[String] = o.map(_.id)

    private lazy val _pluginQuery = PayolaDB.pluginsDataSources.right(this)

    private lazy val _ownerQuery = PayolaDB.dataSourcesOwnership.right(this)

    private lazy val _booleanParameterValuesQuery = PayolaDB.booleanParameterValuesOfDataSources.left(this)

    private lazy val _floatParameterValuesQuery = PayolaDB.floatParameterValuesOfDataSources.left(this)

    private lazy val _intParameterValuesQuery = PayolaDB.intParameterValuesOfDataSources.left(this)

    private lazy val _stringParameterValuesQuery = PayolaDB.stringParameterValuesOfDataSources.left(this)

    @Transient
    private var _parameterValuesLoaded = false

    @Transient
    // This field represents val _parameterValues in common.PluginInstance - it cannot be overriden because it is immutable
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
        if (_owner == None){
            if (ownerId != null && ownerId.isDefined) {
                _owner = evaluateCollection(_ownerQuery).headOption
            }
        }

        _owner
    }

    override def parameterValues: collection.immutable.Seq[PluginType#ParameterValueType] = {
        if (!_parameterValuesLoaded ){
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
