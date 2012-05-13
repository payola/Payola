package cz.payola.data.entities.sources

import cz.payola.data.entities.User

/**
  * A data source representing a publicly accessible sparql endpoint.
  * @param name Name of the data source.
  * @param owner Owner of the data source.
  * @param endpointUrl URL of the publicly accessible sparql endpoint.
  */
class SparqlEndpointDataSource(name: String, owner: Option[User], endpointUrl: String)
    extends cz.payola.domain.entities.sources.SparqlEndpointDataSource(name, owner, endpointUrl)
