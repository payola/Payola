package cz.payola.data

import scala.io.Source

class FakeTtlDataSource extends sparql.providers.SingleSourceRdfDataProvider {
    override def executeSparqlQuery(sparqlQuery: String): String = {
        val source = Source.fromURL(getClass.getResource("/data.ttl"));
        val result = new StringBuilder();

        source.foreach(char => result.append(char));

        return result.toString();
    }
}