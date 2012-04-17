package cz.payola.data.rdf.configurations

import cz.payola.data.rdf.providers.SparqlDataProvider
import cz.payola.data.rdf.ProviderConfiguration

case class SparqlEndpointConfiguration(endpointUrl: String ="http://dbpedia.org/sparql" +
    "?default-graph-uri=http%3A%2F%2Fdbpedia.org" +
    "&format=application%2Frdf%2Bxml" +
    "&save=display")
    extends ProviderConfiguration[SparqlDataProvider]
{
    def createProvider: SparqlDataProvider = {
        new SparqlDataProvider(endpointUrl)
    }
}
