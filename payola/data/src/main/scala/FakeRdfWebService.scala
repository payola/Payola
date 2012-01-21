package cz.payola.data

import scala.io.Source

class FakeRdfWebService extends IPayolaWebService {
    def evaluateSparqlQuery(query: String): String = {
        val source = Source.fromURL(getClass.getResource("/data.xml"));
        val result = new StringBuilder();

        source.foreach(line => result.append(line));

        return result.toString();
    }
}