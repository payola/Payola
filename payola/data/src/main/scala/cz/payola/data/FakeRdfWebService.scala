package cz.payola.data

import scala.io.Source
import util.Random
import java.util.{Calendar, Date}

class FakeRdfWebService extends IPayolaWebService {
    def evaluateSparqlQuery(query: String): String = {
        val calendar: Calendar = Calendar.getInstance()
        val generator: Random = new Random(calendar.getTimeInMillis)

        val sourcePaths: Array[String] = Array("/data.xml", "/data2.xml")
        
        val sourcePath: String = sourcePaths(generator.nextInt(sourcePaths.length))
        val source = Source.fromURL(getClass.getResource(sourcePath));
        val result = new StringBuilder();

        source.foreach(line => result.append(line));

        return result.toString();
    }
}
