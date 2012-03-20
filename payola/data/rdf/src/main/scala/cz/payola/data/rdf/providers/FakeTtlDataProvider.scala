package cz.payola.data.rdf.providers

import scala.io.Source

class FakeTtlDataProvider extends SingleDataProvider
{
    override protected def executeQuery(sparqlQuery: String): String = {
        Source.fromURL(getClass.getResource("/data.ttl")).mkString
    }
}
