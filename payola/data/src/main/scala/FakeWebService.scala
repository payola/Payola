package cz.payola.data

import scala.io.Source

class FakeWebService extends IPayolaWebService {
    def evaluateSparqlQuery(query: String): String = {
        val source = Source.fromURL(getClass.getResource("/data.xml"));
        val result = new StringBuilder();

        for (val line <- source.getLines()) {
            result.append(line);
        }

        return result.toString();
    }
}