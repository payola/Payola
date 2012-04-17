package cz.payola.model

import cz.payola.common.rdf.Graph
import cz.payola.data.rdf.configurations.SparqlEndpointConfiguration
import cz.payola.data.rdf.QueryExecutor
import cz.payola.domain.rdf.RDFGraph
import cz.payola.data.entities.User
import collection.mutable.Seq
import collection.Seq
import cz.payola.data.entities.dao.{AnalysisDAO, UserDAO}

class DataFacade
{
    val userDAO = new UserDAO
    val analysisDAO = new AnalysisDAO

    def getGraph(uri: String): Graph = {
        val dbPediaEndpointUrl = "http://dbpedia.org/sparql" +
            "?default-graph-uri=http%3A%2F%2Fdbpedia.org" +
            "&format=application%2Frdf%2Bxml" +
            "&save=display"
        val configurations = List(new SparqlEndpointConfiguration(dbPediaEndpointUrl))

        val query = """
            CONSTRUCT {
                <%s> ?p1 ?n1 .
                ?n1 ?p2 ?n2 .
            }
            WHERE {
                <%s> ?p1 ?n1 .
                OPTIONAL { ?n1 ?p2 ?n2 }
            }
            LIMIT 40
        """.format(uri, uri)

        QueryExecutor.executeQuery(configurations, query).data.headOption.map(rdf => RDFGraph(rdf)).get
    }

    def getUserByCredentials(username: String, password: String) : Option[User] = {
        userDAO.getUserByCredentials(username, cryptPassword(password))
    }

    def getUserByUsername(username: String) : Option[User] = {
        userDAO.getUserByUsername(username)
    }

    def register(username: String, password: String): Boolean = {
        val u = new User(username, username, cryptPassword(password), username)

        userDAO.persist(u) match {
            case user:User => true
            case _ => false //TODO decide what to do here when the user is not inserted but updated (Unit returned)
        }
    }

    def getPublicAnalysesByOwner(o: User) = {
        analysisDAO.getPublicAnalysesByOwner(o)
    }

    private def cryptPassword(password: String, method: String = "SHA-1") : String = {
        val md = java.security.MessageDigest.getInstance(method)
        val digest = md.digest(password.toCharArray.map(_.toByte))
        new String(digest.map(_.toChar))
    }
}
