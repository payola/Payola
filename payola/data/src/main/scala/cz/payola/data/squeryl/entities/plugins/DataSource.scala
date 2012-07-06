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
                val dataSource = new DataSource(e.id, e.name, e.owner.map(User(_)), dataFetcher,
                    e.parameterValues.map(ParameterValue(_)))
                // Create relation between plugin and this DataSource TODO why there? this should just convert it.
                PluginDbRepresentation(dataFetcher).registerDataSource(dataSource)
                Some(dataSource)
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
    with PersistableEntity
{
    var pluginId: Option[String] = if (df == null) None else Some(df.id)

    var ownerId: Option[String] = o.map(_.id)

    private lazy val _pluginQuery = context.schema.pluginsDataSources.right(this)

    private lazy val _ownerQuery = context.schema.dataSourceOwnership.right(this)

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
