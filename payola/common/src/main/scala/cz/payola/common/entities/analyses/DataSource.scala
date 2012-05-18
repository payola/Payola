package cz.payola.common.entities.analyses

import cz.payola.common.entities._

/**
  * A data source providing RDF data.
  */
trait DataSource extends PluginInstance with OptionallyOwnedEntity with NamedEntity with ShareableEntity
