package controllers

import cz.payola.domain.rdf._
import play.api.mvc._
import cz.payola.web.shared._
import cz.payola.domain.entities.User
import controllers.helpers.Secured
import cz.payola.domain.entities.analyses.evaluation.Success
import cz.payola.common.PayolaException

object GraphDownloader extends PayolaController with Secured
{
    def downloadAnalysisEvaluationResultAsXML(analysisID: String, evaluationID: String) = {
        maybeAuthenticated { u: Option[User] =>
            downloadAnalysisEvaluationResultAs(analysisID, evaluationID, RdfRepresentation.RdfXml, u)
        }
    }

    def downloadAnalysisEvaluationResultAsTTL(analysisID: String, evaluationID: String) = {
        maybeAuthenticated { u: Option[User] =>
            downloadAnalysisEvaluationResultAs(analysisID, evaluationID, RdfRepresentation.Turtle, u)
        }
    }

    private def downloadAnalysisEvaluationResultAs(analysisID: String, evaluationID: String,
        format: RdfRepresentation.Type, u: Option[User]): Result = {
        val success = getAnalysisSuccessForEvaluationID(evaluationID, u)
        val analysis = Payola.model.analysisModel.getAccessibleToUserById(u, analysisID)
        if (success.isDefined && analysis.isDefined) {
            val graph = success.get.outputGraph

            val mimeType = format match {
                case RdfRepresentation.RdfXml => "application/rdf+xml"
                case RdfRepresentation.Turtle => "text/turtle"
                case _ => throw new PayolaException("Unsupported RDF Format")
            }
            val fileExtension = format match {
                case RdfRepresentation.RdfXml => "rdf"
                case RdfRepresentation.Turtle => "ttl"
            }

            Ok(graph.toStringRepresentation(format)).as(mimeType).withHeaders {
                CONTENT_DISPOSITION -> "attachment; filename=%s.%s".format(analysis.get.name, fileExtension)
            }
        } else {
            NotFound(views.html.errors.err404("The data source does not exist."))
        }
    }

    private def getAnalysisEvaluationForID(id: String, user: Option[User]) = {
        Some(Payola.model.analysisModel.getEvaluationTupleForIDAndPerformSecurityChecks(id, user)._2)
    }

    private def getAnalysisSuccessForEvaluationID(id: String, user: Option[User]) = {
        getAnalysisEvaluationForID(id, user).flatMap(_.getResult).collect { case s: Success => s }
    }
}
