package cz.payola.data

import scala.io.Source
import sparql.providers.SingleDataProvider
import util.Random

class FakeDataProvider extends SingleDataProvider
{
    override protected def executeQuery(sparqlQuery: String): String = {
        val generator = new Random(java.util.Calendar.getInstance().getTimeInMillis())
        val sourcePaths = Array("/data.xml", "/data2.xml")
        val sourcePath = sourcePaths(generator.nextInt(sourcePaths.length))
        Source.fromURL(getClass.getResource(sourcePath)).mkString
    }
}
