package cz.payola.domain.entities.sources

import cz.payola.domain.entities.{User, DataSource}

class SparqlEndpointDataSource(name: String, owner: Option[User], val endpointUrl: String)
    extends DataSource(name, owner)
