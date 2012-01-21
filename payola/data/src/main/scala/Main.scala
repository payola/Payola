package cz.payola.data

/**
 * Main object with main method for project running
 *
 * User: Ondřej Heřmánek
 * Date: 3.1.12, 12:50
 */
object Main {
    /**
     * Main method of data project
     */
    def main(args: Array[String]) = {
        val manager = new WebServicesManager();

        // Load available webs services
        manager.initWebServices();

        val result = manager.evaluateSparqlQuery("");

        println(result.getRdf());
        println(result.getTtl());
    }
}
