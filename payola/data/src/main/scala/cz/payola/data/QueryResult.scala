package cz.payola.data

class QueryResult(rdf: String, ttl: String) {
    
    def getRdf(): String = {
        return rdf;
    }

    def getTtl(): String = {
        return ttl;
    }
    
    /**
     * Gets query result splitted into XML nodes. Each node represents one RDF triple.
     *
     * @return returns list of XML nodes in String.
     */
    def getResultInNodes(): List[String] = {
        return spiltQueryResultToTriples();
    }

    /**
     * Expects result to be a XML file content and its nodes are RDF triples.     *
     *
     * @return returns list of nodes, if text has no XML nodes, returns empty list.
     */
    private def spiltQueryResultToTriples(): List[String] = {
        val xmlText = xml.XML.loadString("");

        return null;
    }
}