package cz.payola.data;

/**
 * Manages communication with web services - with all payola web services and local web service.
 *
 * User: Ondra Heřmánek
 * Date: 15.12.11, 19:21
 */
class WebServicesManager extends IPayolaWebService with ILocalWebService {
    var webServices = List[IPayolaWebService](new FakeWebService());

    /**
     * Evaluates given SPARQL query.
     *
     * @param query - SPARQL query
     *
     * @return returns result in String.
     */
    def evaluateSparqlQuery(query: String) : String = {
        val result = new StringBuilder();

        for(val service <- this.webServices){
            result.append(service.evaluateSparqlQuery(query));
        }

        return result.toString();
    }

    /**
     * Gets items related to given item by specified relation type.
     *
     * @param id - ID of item to search related items for
     * @param relationType - relation type of related items
     *
     * @return returns result in String
     */
    def getRelatedItems(id: String, relationType: String) : String = {
        val result = new StringBuilder();

        for(val service <- this.webServices){
            result.append(service.getRelatedItems("", relationType));
        }

        return result.toString();
    }

    /**
     * Expects text to be a XML file content and its nodes are RDF triples.
     *
     * @param text - text with XML file content
     *
     * @return returns list of nodes. if text has no XML nodes, returns empty list.
     */
    def spiltQueryResultToTriples(text: String): List[String] = {
        val xmlText = xml.XML.loadString(text);
        

    }
}
