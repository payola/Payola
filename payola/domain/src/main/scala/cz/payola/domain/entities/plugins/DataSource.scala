package cz.payola.domain.entities.plugins

import collection.immutable
import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins.concrete.DataFetcher
import cz.payola.domain.rdf.Graph
import cz.payola.common.rdf.Edge

object DataSource
{
    /**
      * Creates a new data source based on the specified data fetcher plugin instance.
      * @param name Name of the data source.
      * @param owner Owner of the data source.
      * @param instance The data fetcher plugin instance to base the data source on.
      * @return The data source.
      */
    def apply(name: String, owner: Option[User], instance: PluginInstance): DataSource = {
        instance.plugin match {
            case dataFetcher: DataFetcher => new DataSource(name, owner, dataFetcher, instance.parameterValues)
            case _ => throw new PluginException("The DataSource has to correspond to a DataFetcher plugin.")
        }
    }
}

/**
  * @param _name Name of the data source.
  * @param _owner Owner of the data source.
  * @param dataFetcher The data fetcher plugin corresponding to the data source.
  * @param parameterValues The corresponding data fetcher plugin parameter values.
  */
class DataSource(protected var _name: String, protected var _owner: Option[User], dataFetcher: DataFetcher,
    parameterValues: immutable.Seq[ParameterValue[_]])
    extends PluginInstance(dataFetcher, parameterValues)
    with OptionallyOwnedEntity
    with NamedEntity
    with cz.payola.common.entities.plugins.DataSource
{
    /**
      * Executes the specified query.
      * @param query The query to execute.
      * @return The result of the query.
      */
    def executeQuery(query: String): Graph = {
        plugin.asInstanceOf[DataFetcher].executeQuery(this, query)
    }

    /**
      * Returns the first available triple.
      */
    def getFirstTriple: Option[Edge] = {
        plugin.asInstanceOf[DataFetcher].getFirstTriple(this).edges.headOption
    }

    /**
      * Returns neighbourhood of the specified vertex.
      * @param vertexURI URI of the vertex whose neighbourhood should be returned.
      * @return The neighbourhood graph.
      */
    def getNeighbourhood(vertexURI: String): Graph = {
        plugin.asInstanceOf[DataFetcher].getNeighbourhood(this, vertexURI)
    }

    /**
     * Returns a plugin instance corresponding to the DataSource.
     */
    def toInstance: PluginInstance = {
        val instance = plugin.createInstance()
        plugin.parameters.foreach(n => getParameter(n.name).foreach(instance.setParameter(n.name, _)))
        instance.isEditable = isEditable
        instance
    }

    override def canEqual(other: Any): Boolean = {
        other.isInstanceOf[DataSource]
    }

    override protected def checkInvariants() {
        super[NamedEntity].checkInvariants()
        super[OptionallyOwnedEntity].checkInvariants()
    }
}
