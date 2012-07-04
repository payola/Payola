package cz.payola.domain.virtuoso

import scala.collection.mutable
import scala.io.Source
import java.sql._

/**
  * A Virtuoso data store that performs operations above both of the RDF data store and the Virtuoso SQL database.
  * @param server The Virtuoso server.
  * @param endpointPort Port of the Virtuoso SPARQL endpoint.
  * @param endpointSSL Whether the SSL is used when accessing the SPARQL endpoint.
  * @param sqlPort The Virtuoso SQL database port.
  * @param sqlUsername User name for the Virtuoso SQL database.
  * @param sqlPassword Password for the Virtuoso SQL database.
  */
class VirtuosoStorage(
    val server: String = "localhost",
    val endpointPort: Int = 8890,
    val endpointSSL: Boolean = false,
    val sqlPort: Int = 1111,
    val sqlUsername: String = "dba",
    val sqlPassword: String = "dba")
{
    // Register the driver in the driver manager.
    Class.forName("virtuoso.jdbc3.Driver")

    val endpointUrl = "%s://%s:%s/sparql".format(if (endpointSSL) "https" else "http", server, endpointPort)

    /**
      * Creates a new graph group.
      * @param groupURI Group URI.
      */
    def createGroup(groupURI: String) {
        executeSQLQuery("DB.DBA.RDF_GRAPH_GROUP_CREATE('%s', 1)".format(escapeString(groupURI)))
    }

    /**
      * Deletes the specified graph group. Graphs within that group stay untouched.
      * @param groupURI URI of the group to delete.
      */
    def deleteGroup(groupURI: String) {
        executeSQLQuery("DB.DBA.RDF_GRAPH_GROUP_DROP('%s', 1)".format(escapeString(groupURI)))
    }

    /**
      * Stores the graph in the Virtuoso triple store.
      * @param rdfXml XML representation of the graph.
      * @param graphURI URI of the graph.
      */
    def storeGraph(rdfXml: String, graphURI: String) {
        executeSQLQuery("DB.DBA.RDF_LOAD_RDFXML('%s', '', '%s')".format(escapeString(rdfXml), escapeString(graphURI)))
    }

    /**
      * Adds a graph with the specified URI to the group with groupURI. Graph with this URI
      * must already exist on the server.
      *
      * @param graphURI URI of the graph.
      * @param groupURI URI of the group.
      */
    def addGraphToGroup(graphURI: String, groupURI: String) {
        executeSQLQuery("DB.DBA.RDF_GRAPH_GROUP_INS('" + groupURI + "', '" + graphURI + "')")
    }

    /** Adds a graph to group. First, it stores the graph described by rdfXML and then
      * the graph is added to the group.
      *
      * @param graphURI Graph URI.
      * @param rdfXML RDF XML representation of the graph.
      * @param groupURI URI of the group.
      */
    def addGraphToGroup(rdfXML: String, graphURI: String, groupURI: String) {
        storeGraph(rdfXML, graphURI)
        addGraphToGroup(graphURI, groupURI)
    }

    /** Deletes a graph from the Virtuoso instance.
      *
      * @param graphURI URI of the graph to delete.
      */
    def deleteGraph(graphURI: String) {
        executeSPARQLQuery("CLEAR GRAPH <" + graphURI + ">")
    }

    /** Executes a SPARQL query.
      *
      * @param query Query.
      * @return Output RDF/XML string of the query.
      */
    protected def executeSPARQLQuery(query: String): String = {
        // TODO: decide whether to thow our own VirtuosoException, or keep the regular IO, ... exception

        val urlPrefix = if (endpointSSL) "https" else "http"
        val queryURL = urlPrefix + "://" + server + ":" + endpointPort + "/sparql?query=" + java.net.URLEncoder.encode(query, "UTF-8")
        val connection = new java.net.URL(queryURL).openConnection()
        val requestProperties = Map(
            "Accept" -> "application/rdf+xml"
        )

        requestProperties.foreach(p => connection.setRequestProperty(p._1, p._2))

        val inputStream = connection.getInputStream
        val theString: String = Source.fromInputStream(inputStream, "UTF-8").mkString

        theString
    }

    /** Selects all graphs within that group and returns a RDF XML string.
      *
      * @param groupURI URI of the group.
      * @return RDF XML string representing all the graphs within that group.
      */
    def selectAllInGroup(groupURI: String): String = {
        executeSPARQLQuery("CONSTRUCT { ?s ?p ?o } FROM <" + groupURI + "> WHERE { ?s ?p ?o }")
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
        val connection = createSQLConnection()
        try {
            val statement = connection.createStatement
            try {
                statement.executeQuery(query)
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
