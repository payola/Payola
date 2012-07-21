package controllers

import cz.payola.domain.rdf._
import play.api.mvc.Action
import scala.io.Source
import cz.payola.common.rdf._
import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.analyses.evaluation.Success
import cz.payola.domain.net.Downloader

object GraphDownloader extends PayolaController
{

    def downloadAnalysisResult(result: Success, representation: RdfRepresentation.Type = RdfRepresentation.RdfXml, fileName: String = "graph") = {
        representation match {
            case RdfRepresentation.RdfXml => downloadGraphAsRDF(result.outputGraph, fileName)
            case RdfRepresentation.Turtle => downloadGraphAsTTL(result.outputGraph, fileName)
        }
    }

    def downloadGraphAsRDF(graph: Graph, fileName: String = "graph") = downloadGraphAsRepresentation(graph, RdfRepresentation.RdfXml, "application/rdf+xml", fileName, "xml")

    def downloadGraphAsRepresentation(graph: Graph, representation: RdfRepresentation.Type, mimeType: String, fileName: String, fileExtension: String) = Action {
        val stringRepresentation = graph.textualRepresentation(RdfRepresentation.RdfXml)
        val source = Source.fromString(stringRepresentation)
        val byteArray = source.map(_.toByte).toArray
        source.close()
        Ok(byteArray).as(mimeType).withHeaders {
            CONTENT_DISPOSITION -> "attachment; filename=%s.%s".format(fileName, fileExtension)
        }
    }

    def downloadGraphAsTTL(graph: Graph, fileName: String = "graph") = downloadGraphAsRepresentation(graph, RdfRepresentation.RdfXml, "text/turtle", fileName, "ttl")

    def test() = {
        val downloader = new Downloader("http://tmp.charliemonroe.net/sample.rdf")
        val rdfString = downloader.result
        val g = Graph(RdfRepresentation.RdfXml, rdfString)
        downloadGraphAsRDF(g)
    }

}
