package cz.payola.data

import scala.io.Source
import util.Random

class FakeRdfDataSource extends sparql.providers.SingleSourceRdfDataProvider {
    override def executeSparqlQuery(sparqlQuery: String): String = {
        val generator: Random =
            new Random(java.util.Calendar.getInstance().getTimeInMillis())

        val sourcePaths: Array[String] = Array("/data.xml", "/data2.xml")
        
        val sourcePath: String = sourcePaths(generator.nextInt(sourcePaths.length))
        val source = Source.fromURL(getClass.getResource(sourcePath));
        val result = new StringBuilder();

        source.foreach(char => result.append(char));

        return result.toString();
    }
}
