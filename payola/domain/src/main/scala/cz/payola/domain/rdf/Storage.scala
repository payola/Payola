package cz.payola.domain.rdf

import com.hp.hpl.jena.query.QueryFactory
import java.io._

trait Storage
{
    /**
      * Creates a new graph group.
      * @param groupURI Group URI.
      */
    def createGroup(groupURI: String)

    /**
      * Deletes the specified graph group. Graphs within that group stay untouched.
      * @param groupURI URI of the group to delete.
      */
    def deleteGroup(groupURI: String)

    /**
      * Stores the graph in the storage.
      * @param graphURI URI of the graph.
      * @param rdfXml XML representation of the graph.
      */
    def storeGraph(graphURI: String, rdfXml: String)

    /**
      * Stores the graph in the storage.
      * @param graphURI URI of the graph.
      * @param graphURL URL at which to fetch the graph.
      */
    def storeGraphAtURL(graphURI: String, graphURL: String)

    /**
     * Stores the graph in the storage.
     * @param graphURI URI of the graph.
     * @param file File with RDF/XML or TTL.
     */
    def storeGraphFromFile(graphURI: String, file: File, fileType: RdfRepresentation.Type)

    def storeGraphGraphProtocol(graphURI: String, graph: Graph)

    /**
      * Adds a graph with the specified URI to the specified group. A graph with must already exist on the server.
      * @param graphURI URI of the graph.
      * @param groupURI URI of the group.
      */
    def addGraphToGroup(graphURI: String, groupURI: String)

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
    def deleteGraph(graphURI: String)

    /**
      * Executes the specified SPARQL query.
      * @param query The query to execute.
      * @return The resulting graph.
      */
    def executeSPARQLQuery(query: String): Graph

    def executeSPARQLQueryJena(query: String): com.hp.hpl.jena.query.Dataset

    /**
      * Executes the specified SPARQL query over data in the specifed group.
      * @param query The query to execute.
      * @param groupURI URI of the group whose data should be queried.
      * @return The resulting graph.
      */
    def executeSPARQLQuery(query: String, groupURI: String, setResultsCount: Option[Long] = None): Graph = {
        val sparqlQuery = QueryFactory.create(query)
        sparqlQuery.addGraphURI(groupURI)
        val graph: Graph = executeSPARQLQuery(sparqlQuery.toString)

        if(setResultsCount.isDefined) {
            graph.resultsCount = setResultsCount
        }

        graph
    }

    def executeSPARQLQueryJena(query: String, groupURI: String): com.hp.hpl.jena.query.Dataset = {
        val sparqlQuery = QueryFactory.create(query)
        sparqlQuery.addGraphURI(groupURI)
        executeSPARQLQueryJena(sparqlQuery.toString)
    }
}
