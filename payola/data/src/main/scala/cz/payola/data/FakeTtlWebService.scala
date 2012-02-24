package cz.payola.data

import scala.io.Source

class FakeTtlWebService(manager : WebServicesManager) extends WebServiceBase(manager) {
    override def evaluateSparqlQuery(query: String): String = {
        val source = Source.fromURL(getClass.getResource("/data.ttl"));
        val result = new StringBuilder();

        source.foreach(char => result.append(char));

        return result.toString();
    }
}