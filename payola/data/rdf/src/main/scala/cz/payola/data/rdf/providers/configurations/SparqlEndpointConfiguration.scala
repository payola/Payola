package cz.payola.data.rdf.providers.configurations

case class SparqlEndpointConfiguration(
    val url: String = "http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&format=application%2Frdf%2Bxml&save=display")
    extends ProviderConfiguration()
{
}