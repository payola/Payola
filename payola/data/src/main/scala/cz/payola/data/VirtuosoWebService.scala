package cz.payola.data

import scala.collection.mutable
import scala.io.Source
import java.util.Properties
import util.control.Exception

class VirtuosoWebService(manager : WebServicesManager) extends WebServiceBase(manager) {
    val request : String = "{protocol}://{host}/sparql?{defaultUri}&{namedUri}&{query}&{format}&save=display";
    val protocol : String = "http";
    var host : String = "";
    val defaultUri : String = "default-graph-uri=";
    val namedUri : String = "named-graph-uri=";
    val queryFormat : String = "query={query}";
    val format : String = "format=application%2Frdf%2Bxml"

    override def initialize() = {
        // Start actor for parallel query processing
        super.initialize();

        // Read ini file with properties
        val prop : Properties = new Properties();
        prop.load(getClass.getResource("/virtuoso.ini").openStream());

        val h : String = prop.getProperty("host");
        val p : String = prop.getProperty("port");

        // Host is composed from ini file values (port may be undefined)
        if (p != null && p.size > 0)
            host = h + ":" + p;
        else
            host = h;
    }

    override def evaluateSparqlQuery(query: String): String = {
        val result = new StringBuilder();

        // Query is composed and URL Coded
        val request = composeQueryRequest(query);

        // Read query result
        try
        {
            val source = Source.fromURL(request);
            source.foreach(char => result.append(char));
            // Log error
        } catch {
            case ex : Exception =>
                manager.logError("Virtuoso service error for query:" + query);
        }

        // Return query result
        return result.toString();
    }
    
    private def composeQueryRequest (query: String) : String = {
        val q : String =
            queryFormat.replaceAllLiterally(
                "{query}",
                java.net.URLEncoder.encode(query, "UTF-8")
        );
        
        return request.replaceAllLiterally("{protocol}", protocol)
            .replaceAllLiterally("{host}", host)
                .replaceAllLiterally("{defaultUri}", defaultUri)
                    .replaceAllLiterally("{namedUri}", namedUri)
                        .replaceAllLiterally("{query}", q)
                            .replaceAllLiterally("{format}", format);
    }
}