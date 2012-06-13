package cz.payola.domain.entities.analyses

import cz.payola.domain.entities._
import scala.collection.immutable
import cz.payola.domain.entities.analyses.plugins.DataFetcher
import cz.payola.domain.rdf.Graph

object DataSource
{
    def apply(name: String, owner: Option[User], instance: PluginInstance): DataSource = {
        instance.plugin match {
            case dataFetcher: DataFetcher => new DataSource(name, owner, dataFetcher, instance.parameterValues)
            case _ => throw new PluginException("The DataSource has to correspond to a DataFetcher plugin.")
        }
    }
}

class DataSource(protected var _name: String, protected var _owner: Option[User], plugin: DataFetcher,
    parameterValues: immutable.Seq[ParameterValue[_]])
    extends PluginInstance(plugin, parameterValues)
    with OptionallyOwnedEntity
    with NamedEntity
    with ShareableEntity
    with cz.payola.common.entities.analyses.DataSource
{
    /**
      * Executes the specified query.
      * @param query The query to execute.
      * @return The result of the query.
      */
    def executeQuery(query: String): Graph = {
        plugin.executeQuery(this, query)
    }

    /**
      * Returns neighbourhood of the specified node.
      * @param nodeURI URI of the node whose neighbourhood should be returned.
      * @param distance Maximal distance to travel from the node to its neighbours. To select only direct neighbours,
      *                 use 1, to select direct neighbours and their neighbours, use 2 etc. Note that particular data
      *                 fetchers may use some optimizations/heuristics so it's not guaranteed that this parameter will
      *                 be always taken into account.
      * @return The neighbourhood graph.
      */
    def getNeighbourhood(nodeURI: String, distance: Int = 1): Graph = {
        plugin.getNeighbourhood(this, nodeURI, distance)
    }

    override def canEqual(other: Any): Boolean = {
        other.isInstanceOf[DataSource]
    }

    override protected def checkInvariants() {
        super[PluginInstance].checkInvariants()
        super[OptionallyOwnedEntity].checkInvariants()
        super[NamedEntity].checkInvariants()
    }
}
