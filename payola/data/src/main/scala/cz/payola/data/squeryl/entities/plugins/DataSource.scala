package cz.payola.data.squeryl.entities.plugins

import scala.collection.immutable
import cz.payola.data.squeryl.entities._
import cz.payola.data.squeryl.SquerylDataContextComponent
import cz.payola.domain.entities.plugins.concrete.DataFetcher

/**
  * This object converts [[cz.payola.common.entities.plugins.DataSource]]
  * to [[cz.payola.data.squeryl.entities.plugins.DataSource]]
  */
object DataSource extends EntityConverter[DataSource]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[DataSource] = {
        entity match {
            case e: DataSource => Some(e)
            case e: cz.payola.domain.entities.plugins.DataSource => {
                val dataFetcher = e.plugin.asInstanceOf[DataFetcher]
                Some(new DataSource(e.id, e.name, e.owner.map(User(_)),
                    dataFetcher, e.parameterValues.map(ParameterValue(_)), e.isPublic, e.description, e.isEditable))
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
    paramValues: immutable.Seq[ParameterValue[_]],
    var _isPub: Boolean,
    var _desc: String,
    var _isEdit: Boolean)
    (implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.plugins.DataSource(n, o, df, paramValues)
    with PersistableEntity with OptionallyOwnedEntity with ShareableEntity with NamedEntity with DescribedEntity
    with PluginInstanceLike
{
    var pluginId: String = Option(df).map(_.id).getOrElse(null)
    
    override def plugin = {
        if (_plugin == null){
            wrapInTransaction {
                context.dataSourceRepository.loadPlugin(this)
            }
        }

        _plugin
    }

    override def parameterValues: collection.immutable.Seq[PluginType#ParameterValueType] = {
        // Really check for plugin - when plugin is null
        // parameterValues are definitely not loaded and mapped to parameters
        if (_plugin == null) {
            wrapInTransaction {
                context.dataSourceRepository.loadParameterValues(this)
            }
        }

        _parameterValues
    }
}
