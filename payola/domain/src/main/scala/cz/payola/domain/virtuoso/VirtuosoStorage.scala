package cz.payola.domain.virtuoso

import scala.io.Source

// e.g. http(s)://server:port/path/sparql
class VirtuosoStorage(val server: String, val port: Int, val useSSL: Boolean = false, sqlUsername: String, sqlPassword: String)
{
    // Creates a SQL connection. It's up to the object whether it keeps the connection
    // permanently, or creates a new one each time
    private val sqlConnection = new VirtuosoSQLConnection()

    /** Adds graph with graphURI to the group with groupURI. Graph with this URI
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

    /** Creates a new graph group.
      *
      * @param groupURI Group URI.
      */
    def createGroup(groupURI: String) {
        executeSQLQuery("DB.DBA.RDF_GRAPH_GROUP_CREATE('" + groupURI + "', 1)")
    }

    /** Deletes a graph from the Virtuoso instance.
      *
      * @param graphURI URI of the graph to delete.
      */
    def deleteGraph(graphURI: String) {
        executeSPARQLQuery("CLEAR GRAPH <" + graphURI + ">")
    }

    /** Deletes a graph group. Graphs within that group stay untouched.
      *
      * @param groupURI URI of the group to delete.
      */
    def deleteGroup(groupURI: String) {
        executeSQLQuery("DB.DBA.RDF_GRAPH_GROUP_DROP('" + groupURI + "', 1)")
    }

    /** Executes a SQL query.
      *
      * @param query Query.
      * @return Output string of the query. Just for debug purposes.
      */
    protected def executeSQLQuery(query: String): String =  {
        // TODO: decide whether to thow our own VirtuosoException, or keep the regular IO, ... exception
        sqlConnection.executeQuery(query)
    }

    /** Executes a SPARQL query.
      *
      * @param query Query.
      * @return Output RDF/XML string of the query.
      */
    protected def executeSPARQLQuery(query: String): String = {
        // TODO: decide whether to thow our own VirtuosoException, or keep the regular IO, ... exception

        val urlPrefix = if (useSSL) "https" else "http"
        val queryURL = urlPrefix + "://" + server + ":" + port + "/sparql?query=" + java.net.URLEncoder.encode(query, "UTF-8")
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

    /** Stores the graph in Virtuoso.
      *
      * @param rdfXML XML representation of the graph.
      * @param graphURI URI of the graph.
      */
    def storeGraph(rdfXML: String, graphURI: String) {
        executeSQLQuery("DB.DBA.RDF_LOAD_RDFXML('" + rdfXML.replaceAllLiterally("'", "\\'") + "', '', '" + graphURI + "')")
    }


}
