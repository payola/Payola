package cz.payola.data

import scala.io.Source
import sparql.providers.SingleDataProvider

class FakeTtlDataProvider extends SingleDataProvider
{
    override protected def executeQuery(sparqlQuery: String): String = {
        Source.fromURL(getClass.getResource("/data.ttl")).mkString
    }
}
