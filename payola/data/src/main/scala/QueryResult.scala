package cz.payola.data

/**
 * Class represents result of Sparql query.
 *
 * User: Ondřej Heřmánek
 * Date: 3.1.12, 13:14
 */
class QueryResult(result: String) {
    /**
     * Gets whole query result in XML format as String.
     *
     * @return returns query result
     */
    def getResult(): String = {
        return this.result;
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
        val xmlText = xml.XML.loadString(result);

        return null;
    }
}