package controllers

import helpers.Secured
import cz.payola.domain.entities.User
import cz.payola.web.shared.Payola
import play.mvc.Security.Authenticated
import java.io.FileInputStream
import cz.payola.domain.rdf.RdfRepresentation

object Evaluation extends PayolaController with Secured
{
    /*def detail(id: String) = maybeAuthenticatedWithRequest { (user, request) =>
        Ok(views.html.evaluation.detail())
        /*Payola.model.analysisModel.getById(id).map { a =>
            val canTakeOwnership = user.isDefined && a.token.isDefined && request.session.get("analysis-tokens")
                .map { tokens: String =>
                tokens.split(",").contains(a.token.get)
            }.getOrElse(false)

            Ok(views.html.analysis.detail(user, a, canTakeOwnership))
        }.getOrElse {
            NotFound(views.html.errors.err404("The analysis does not exist."))
        }*/
    } */

    def rdf(evaluationId: String) = {
        maybeAuthenticated { u: Option[User] =>
            Ok(scala.io.Source.fromFile("/opt/www/virtuoso/evaluation/"+evaluationId+".rdf", "UTF-8").map(_.toString).mkString).as("application/rdf+xml").withHeaders {
                CONTENT_DISPOSITION -> "attachment; filename=%s.%s".format(evaluationId, "rdf")
            }
        }
    }

    def listCubes(analysisId: String, evaluationId: String) = maybeAuthenticated { user =>
        Payola.model.analysisModel.getAccessibleToUserById(user, analysisId).map{ a =>
            Ok("a")
        }.getOrElse{ throw new Exception("") }
    }

    def delete(id: String) = authenticatedWithRequest { (user, request) =>
        user.ownedAnalyses.find(_.id == id).map(Payola.model.analysisModel.remove(_))
            .getOrElse(NotFound("Analysis not found."))

        Redirect(routes.Analysis.list())
    }

    def list(page: Int = 1) = authenticated { user: User =>
        Payola.model.analysisResultStorageModel
        Ok(views.html.analysis.list(Some(user), user.ownedAnalyses, page))
    }

    def listAccessible(page: Int = 1) = maybeAuthenticated { user: Option[User] =>
        Ok(views.html.analysis
            .list(user, Payola.model.analysisModel.getAccessibleToUser(user), page, Some("Accessible analyses")))
    }

    def listAccessibleByOwner(ownerId: String, page: Int = 1) = maybeAuthenticated { user: Option[User] =>
        val owner = Payola.model.userModel.getById(ownerId)
        val analyses = if (owner.isDefined) {
            Payola.model.analysisModel.getAccessibleToUserByOwner(user, owner.get)
        } else {
            List()
        }
        Ok(views.html.analysis.list(user, analyses, page))
    }
}
