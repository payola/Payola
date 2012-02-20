package cz.payola.data

import scala.io.Source
import util.Random
import java.util.{Calendar, Date}

class FakeRdfWebService extends IPayolaWebService {
    def evaluateSparqlQuery(query: String): String = {
        val generator: Random = new Random(Calendar.getInstance().getTimeInMillis)

        val sourcePaths: Array[String] = Array("/data.xml", "/data2.xml")
        
        val sourcePath: String = sourcePaths(generator.nextInt(sourcePaths.length))
        val source = Source.fromURL(getClass.getResource(sourcePath));
        val result = new StringBuilder();

        source.foreach(char => result.append(char));

        return result.toString();
    }

    def initialize() = {

    }

    def act() = {
    }
}
