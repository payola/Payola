package cz.payola.common.entities.plugins

import cz.payola.common.entities._

/**
  * A data source providing RDF data.
  */
trait DataSource extends PluginInstance with OptionallyOwnedEntity with NamedEntity with ShareableEntity
