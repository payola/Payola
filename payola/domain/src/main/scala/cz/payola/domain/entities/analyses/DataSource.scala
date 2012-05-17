package cz.payola.domain.entities.analyses

import cz.payola.domain.entities._
import scala.collection.immutable
import cz.payola.domain.entities.analyses.plugins.DataFetcher

class DataSource(protected var _name: String, protected val _owner: Option[User], plugin: DataFetcher,
    parameterValues: immutable.Seq[ParameterValue[_]])
    extends PluginInstance(plugin, parameterValues)
    with OptionallyOwnedEntity
    with NamedEntity
    with ShareableEntity
    with cz.payola.common.entities.analyses.DataSource
{
    protected var _isPublic = false

    override def canEqual(other: Any): Boolean = {
        other.isInstanceOf[DataSource]
    }

    override protected def checkInvariants() {
        super[PluginInstance].checkInvariants()
        super[OptionallyOwnedEntity].checkInvariants()
        super[NamedEntity].checkInvariants()
    }
}
