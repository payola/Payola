package cz.payola.data

import scala.io.Source
import java.util.Properties
import scala.actors.Actor

class VirtuosoWebService extends IPayolaWebService {
    val request : String = "{protocol}://{host}/sparql?{defaultUri}&{namedUri}&{query}&{format}&save=display";
    val protocol : String = "http";
    var host : String = "";
    val defaultUri : String = "default-graph-uri=";
    val namedUri : String = "named-graph-uri=";
    val queryFormat : String = "query={query}";
    val format : String = "format=application%2Frdf%2Bxml"
    val defaultQuery :String = "select+distinct+%3FConcept+where+%7B%5B%5D+a+%3FConcept%7D+LIMIT+100";

    def initialize() = {
        // Read ini file with properties
        val prop : Properties = new Properties();
        prop.load(getClass.getResource("/virtuoso.ini").openStream());

        // Host is composed from ini file values
        host = prop.getProperty("host") + ":" + prop.getProperty("port");
    }

    def evaluateSparqlQuery(query: String): String = {
        val result = new StringBuilder();

        // Query is composed and URL Coded
        val request = composeQueryRequest(defaultQuery);

        // Read query result
        val source = Source.fromURL(request);
        source.foreach(char => result.append(char));

        return result.toString();
    }
    
    private def composeQueryRequest (query: String) : String = {
        // TODO: make sure that query is URL coded
        var q : String = queryFormat.replaceAllLiterally("{query}",query);
        
        return request.replaceAllLiterally("{protocol}", protocol)
            .replaceAllLiterally("{host}", host)
                .replaceAllLiterally("{defaultUri}", defaultUri)
                    .replaceAllLiterally("{namedUri}", namedUri)
                        .replaceAllLiterally("{query}", q)
                            .replaceAllLiterally("{format}", format);
    }

    def act() = {
    }
}