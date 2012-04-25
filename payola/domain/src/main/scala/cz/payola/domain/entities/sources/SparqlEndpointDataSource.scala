package cz.payola.domain.entities.sources

import cz.payola.domain.entities.{User, DataSource}

/**
  * A data source representing a publicly accessible sparql endpoint.
  * @param name Name of the data source.
  * @param owner Owner of the data source.
  * @param endpointUrl URL of the publicly accessible sparql endpoint.
  */
class SparqlEndpointDataSource(name: String, owner: Option[User], val endpointUrl: String)
    extends DataSource(name, owner)
