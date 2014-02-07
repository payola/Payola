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
        this("Open Data Clean Storage", 0, List(
            new StringParameter(OpenDataCleanStorage.serviceURLParameter, "", false, false, false, true),
            new StringParameter(OpenDataCleanStorage.endpointURLParameter, "", false, false, false, true)
        ), IDGenerator.newId)
    }

    def getServiceURLParameter(instance: PluginInstance): Option[String] = {
        instance.getStringParameter(OpenDataCleanStorage.serviceURLParameter)
    }

    def getEndpointURLParameter(instance: PluginInstance): Option[String] = {
        instance.getStringParameter(OpenDataCleanStorage.endpointURLParameter)
    }

    def executeQuery(instance: PluginInstance, query: String): Graph = {
        usingDefined(getEndpointURLParameter(instance)) { endpointURL =>
            new SparqlEndpoint(endpointURL).executeQuery(query)
        }
    }

    override def getNeighbourhood(instance: PluginInstance, vertexURI: String): Graph = {

        usingDefined(getServiceURLParameter(instance)) { serviceURL =>
            val neighbourhoodUrl = serviceURL + "/uri?format=trig&uri=" + URLEncoder.encode(vertexURI, "UTF-8")
            JenaGraph(RdfRepresentation.Trig, new Downloader(neighbourhoodUrl, "application/x-trig").result)
        }
    }
}

object OpenDataCleanStorage
{
    val serviceURLParameter = "Output Webservice URL"

    val endpointURLParameter = "Sparql Endpoint URL"
}
