package cz.payola.common.entities.plugins

import cz.payola.common.entities._

/**
  * A plugin instance that can be used as a data source providing RDF data (i.e. executing SPARQL queries, retrieving
  * the vertex neighbourhood etc.).
  */
trait DataSource extends PluginInstance with OptionallyOwnedEntity with NamedEntity with ShareableEntity
{
    override def classNameText = "data source"
}
