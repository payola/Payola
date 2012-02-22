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
        manager.initialize();

        val query = "select distinct ?Concept where {[] a ?Concept} LIMIT 100";
        val result = manager.evaluateSparqlQuery(query);

        println("RDF: " + result.getRdf());
        println("TTL: " + result.getTtl());

        //sys.exit(0);
    }
}
