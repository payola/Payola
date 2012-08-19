package cz.payola.domain.entities.plugins.concrete.data

import scala.collection.immutable
import java.net.URLEncoder
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.concrete.DataFetcher
import cz.payola.domain.entities.plugins.parameters.StringParameter
import cz.payola.domain.rdf._
import cz.payola.domain.net.Downloader

sealed class OpenDataCleanStorage(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends DataFetcher(name, inputCount, parameters, id)
{
    def this() = {
        this("Open Data Clean Storage", 0, List(new StringParameter("Server", "", false)), IDGenerator.newId)
    }

    def executeQuery(instance: PluginInstance, query: String): Graph = {
        usingDefined(instance.getStringParameter("Server")) { server =>
            new SparqlEndpoint(server + "/sparql").executeQuery(query)
        }
    }

    override def getNeighbourhood(instance: PluginInstance, vertexURI: String): Graph = {
        usingDefined(instance.getStringParameter("Server")) { server =>
            val neighbourhoodUrl = server + "/uri?format=trig&uri=" + URLEncoder.encode(vertexURI, "UTF-8")
            Graph(RdfRepresentation.Trig, new Downloader(neighbourhoodUrl).result)
        }
    }
}
