package controllers

import cz.payola.domain.rdf._
import play.api.mvc._
import scala.io.Source
import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.analyses.evaluation.Success
import cz.payola.domain.net.Downloader
import cz.payola.web.shared._
import cz.payola.domain.entities.User
import controllers.helpers.Secured
import cz.payola.domain.entities.analyses.evaluation.Success
import scala.Some
import cz.payola.domain.entities.analyses.evaluation.Success
import scala.Some

object GraphDownloader extends PayolaController with Secured
{

    private def getAnalysisEvaluationForID(id: String, user: Option[User]) = {
        val opt = AnalysisRunner.runningEvaluations.get(id)
        if (opt.isDefined){
            if (opt.get._1 == user){
                Some(opt.get._2)
            }else{
                None
            }
        }else{
            None
        }
    }

    private def getAnalysisSuccessForEvaluationID(id: String, user: Option[User]) = {
        val evaluationOpt = getAnalysisEvaluationForID(id, user)
        if (evaluationOpt.isDefined){
            evaluationOpt.get.getResult match {
                case Some(s: Success) => Some(s)
                case _ => None
            }
        }else{
            None
        }
    }

    def downloadAnalysisEvaluationResultAsXML(analysisID: String, evaluationID: String) = maybeAuthenticated { u: Option[User] =>
        val success = getAnalysisSuccessForEvaluationID(evaluationID, u)
        val analysis = Payola.model.analysisModel.getAccessibleToUserById(u, analysisID)
        if (success.isDefined && analysis.isDefined){
            downloadAnalysisResult(success.get, fileName = analysis.get.name)
        }else{
            NotFound(views.html.errors.err404("The data source does not exist."))
        }
    }

    def downloadAnalysisEvaluationResultAsTTL(analysisID: String, evaluationID: String) = maybeAuthenticated { u: Option[User] =>
        val success = getAnalysisSuccessForEvaluationID(evaluationID, u)
        val analysis = Payola.model.analysisModel.getAccessibleToUserById(u, analysisID)
        if (success.isDefined && analysis.isDefined){
            downloadAnalysisResult(success.get, RdfRepresentation.Turtle, analysis.get.name)
        }else{
            NotFound(views.html.errors.err404("The data source does not exist."))
        }
    }

    def downloadAnalysisResult(result: Success, representation: RdfRepresentation.Type = RdfRepresentation.RdfXml, fileName: String = "graph"): Result = {
        representation match {
            case RdfRepresentation.RdfXml => downloadGraphAsRDF(result.outputGraph, fileName)
            case RdfRepresentation.Turtle => downloadGraphAsTTL(result.outputGraph, fileName)
        }
    }

    def downloadGraphAsRDF(graph: Graph, fileName: String = "graph"): Result = downloadGraphAsRepresentationResult(graph, RdfRepresentation.RdfXml, "application/rdf+xml", fileName, "xml")
    def downloadGraphAsTTL(graph: Graph, fileName: String = "graph"): Result = downloadGraphAsRepresentationResult(graph, RdfRepresentation.RdfXml, "text/turtle", fileName, "ttl")

    def downloadGraphAsRepresentationResult(graph: Graph, representation: RdfRepresentation.Type, mimeType: String, fileName: String, fileExtension: String): Result = {
        val stringRepresentation = graph.textualRepresentation(RdfRepresentation.RdfXml)
        val source = Source.fromString(stringRepresentation)
        val byteArray = source.map(_.toByte).toArray
        source.close()
        Ok(byteArray).as(mimeType).withHeaders {
            CONTENT_DISPOSITION -> "attachment; filename=%s.%s".format(fileName, fileExtension)
        }
    }

    def downloadGraphAsRepresentation(graph: Graph, representation: RdfRepresentation.Type, mimeType: String, fileName: String, fileExtension: String) = Action {
        downloadGraphAsRepresentationResult(graph, representation, mimeType, fileName, fileExtension)
    }
}
