package cz.payola.data

import scala.io.Source

class FakeTtlWebService extends IPayolaWebService {
    def evaluateSparqlQuery(query: String): String = {
        val source = Source.fromURL(getClass.getResource("/data.ttl"));
        val result = new StringBuilder();

        source.foreach(line => result.append(line));

        return result.toString();
    }
}