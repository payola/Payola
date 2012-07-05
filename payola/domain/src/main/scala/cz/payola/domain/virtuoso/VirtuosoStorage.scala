package cz.payola.domain.virtuoso

import java.sql._
import com.hp.hpl.jena.query.QueryFactory
import cz.payola.domain.net.Downloader
import cz.payola.domain.rdf._

/**
  * A Virtuoso data store that performs operations above both of the RDF data store and the Virtuoso SQL database.
  * @param server The Virtuoso server.
  * @param endpointPort Port of the Virtuoso SPARQL endpoint.
  * @param endpointUsesSSL Whether the SSL is used when accessing the SPARQL endpoint.
  * @param sqlPort The Virtuoso SQL database port.
  * @param sqlUsername User name for the Virtuoso SQL database.
  * @param sqlPassword Password for the Virtuoso SQL database.
  */
class VirtuosoStorage(
    val server: String = "localhost",
    val endpointPort: Int = 8890,
    val endpointUsesSSL: Boolean = false,
    val sqlPort: Int = 1111,
    val sqlUsername: String = "dba",
    val sqlPassword: String = "dba")
{
    // Register the driver in the driver manager.
    Class.forName("virtuoso.jdbc3.Driver")

    /**
      * URL of the Virtuoso SPARQL endpoint.
      */
    val endpointURL = "%s://%s:%s/sparql".format(if (endpointUsesSSL) "https" else "http", server, endpointPort)

    /**
      * Creates a new graph group.
      * @param groupURI Group URI.
      */
    def createGroup(groupURI: String) {
        executeSQL("DB.DBA.RDF_GRAPH_GROUP_CREATE('%s', 1)".format(escapeString(groupURI)))
    }

    /**
      * Deletes the specified graph group. Graphs within that group stay untouched.
      * @param groupURI URI of the group to delete.
      */
    def deleteGroup(groupURI: String) {
        executeSQL("DB.DBA.RDF_GRAPH_GROUP_DROP('%s', 1)".format(escapeString(groupURI)))
    }

    /**
      * Stores the graph in the Virtuoso triple store.
      * @param graphURI URI of the graph.
      * @param rdfXml XML representation of the graph.
      */
    def storeGraph(graphURI: String, rdfXml: String) {
        executeSQL("DB.DBA.RDF_LOAD_RDFXML('%s', '', '%s')".format(escapeString(rdfXml), escapeString(graphURI)))
    }

    /**
      * Adds a graph with the specified URI to the specified group. A graph with must already exist on the server.
      * @param graphURI URI of the graph.
      * @param groupURI URI of the group.
      */
    def addGraphToGroup(graphURI: String, groupURI: String) {
        executeSQL("DB.DBA.RDF_GRAPH_GROUP_INS('%s', '%s')".format(escapeString(groupURI), escapeString(graphURI)))
    }

    /**
      * Stores the specified graph to the database and adds it to the specified group.
      * @param graphURI URI of the graph.
      * @param rdfXml XML representation of the graph.
      * @param groupURI URI of the group.
      */
    def addGraphToGroup(graphURI: String, rdfXml: String, groupURI: String) {
        storeGraph(graphURI, rdfXml)
        addGraphToGroup(graphURI, groupURI)
    }

    /**
      * Deletes the specified graph from the database.
      * @param graphURI URI of the graph to delete.
      */
    def deleteGraph(graphURI: String) {
        executeSQLQuery("sparql CLEAR GRAPH <%s>".format(escapeString(graphURI)))
    }

    /**
      * Executes the specified SPARQL query.
      * @param query The query to execute.
      * @return The resulting graph.
      */
    def executeSPARQLQuery(query: String): Graph = {
        val url = endpointURL + "?query=" + java.net.URLEncoder.encode(query, "UTF-8")
        val rdfXml = new Downloader(url, "application/rdf+xml").result
        Graph(RdfRepresentation.RdfXml, rdfXml)
    }

    /**
      * Executes the specified SPARQL query over data in the specifed group.
      * @param query The query to execute.
      * @param groupURI URI of the group whose data should be queried.
      * @return The resulting graph.
      */
    def executeSPARQLQuery(query: String, groupURI: String): Graph = {
        val sparqlQuery = QueryFactory.create(query)
        sparqlQuery.addGraphURI(groupURI)
        executeSPARQLQuery(sparqlQuery.toString)
    }

    /**
      * Creates a connection to the Virtuoso SQL database.
      * @return
      */
    private def createSQLConnection(): Connection = {
        DriverManager.getConnection("jdbc:virtuoso://" + server + ":" + sqlPort, sqlUsername, sqlPassword)
    }

    /**
      * Executes the specified SQL query.
      * @param query The query to execute.
      * @return The query result.
      */
    private def executeSQLQuery(query: String): ResultSet = {
        executeOnSQLStatement(_.executeQuery(query))
    }

    /**
      * Executes the specified batch SQL.
      * @param sql The SQL to execute.
      */
    private def executeSQL(sql: String) {
        executeOnSQLStatement(_.execute(sql))
    }

    /**
      * Executes the specified function on the SQL statement.
      * @param f The function to execute.
      * @tparam A Type of the function return value.
      * @return Return value of the function.
      */
    private def executeOnSQLStatement[A](f: Statement => A): A = {
        val connection = createSQLConnection()
        try {
            val statement = connection.createStatement
            try {
                f(statement)
            } finally {
                statement.close()
            }
        } finally {
            connection.close()
        }
    }

    /**
      * Escapes the specified value so it can be used as a string in SQL queries.
      * @param value The value to escape.
      * @return The escaped value.
      */
    private def escapeString(value: String): String = {
        value.replaceAllLiterally("'", "\\'")
    }
}
