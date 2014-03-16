package controllers

import controllers.helpers.Secured
import cz.payola.domain.entities._
import cz.payola.web.shared.Payola
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.data.Form
import play.api.data.Forms._
import views.html
import cz.payola.common.ValidationException
import cz.payola.web.client.util.UriHashTools

object CacheStore extends PayolaController with Secured
{
    def list(page: Int = 1) = maybeAuthenticatedWithRequest { (user: Option[User], request) =>
    // Get prefixes from logged user / only general prefixes in proper order (first users, then general)
        val cached =
            user.map(_.availableAnalysesResults).getOrElse(
                Payola.model.analysisResultStorageModel.getAllAvailableToUser(None))
                .sortBy(_.analysisId).reverse.sortBy(p => p.owner.map(_.id)).reverse

        Ok(views.html.cachestore.list(user, cached, page)(request.flash))
    }

    def delete(id: String) = authenticated { user =>
        try
        {
            Payola.model.embeddingDescriptionModel.removeByAnalysisId(id)

            val storedResult = user.availableAnalysesResults.find{ stored => stored.id == id }
            if (storedResult.isDefined) {
                user.removeOwnedAnalysisResult(storedResult.get)
                Redirect(routes.CacheStore.list()).flashing("success" -> "The stored analysis result has been successfully removed.")
            }
            else {
                NotFound("The analysis result does not exist.")
            }
        }
        catch {
            case validationExc: ValidationException =>
                Redirect(routes.CacheStore.list()).flashing("error" -> validationExc.message)
        }
    }

    def create(id: String) = authenticated {user =>
        try
        {
            val storedResult = user.availableAnalysesResults.find{ stored => stored.id == id }
            if (storedResult.isDefined) {
                val embeddingDesc = Payola.model.embeddingDescriptionModel.createEmbedded(storedResult.get)

                Redirect(routes.CacheStore.list()).flashing("success" -> "The URI has been successfully created.")
            }
            else {
                NotFound("The analysis result does not exist.")
            }
        }
        catch {
            case validationExc: ValidationException =>
                Redirect(routes.CacheStore.list()).flashing("error" -> validationExc.message)
        }
    }

    def embed(uriHash: String) = authenticated { user =>
        val analysisResult = Payola.model.embeddingDescriptionModel.getEmbeddedAnalysisResult(uriHash)
        if(analysisResult.isDefined) {

            val parameters =
                if(analysisResult.get._2.defaultVisualPlugin.isDefined && analysisResult.get._2.defaultVisualPlugin != "") {
                    "viewPlugin="+analysisResult.get._2.defaultVisualPlugin.get+"&evaluation="+analysisResult.get._1.evaluationId
                } else {
                    "evaluation="+analysisResult.get._1.evaluationId
                }
            Redirect(routes.Analysis.detail(analysisResult.get._1.analysisId+"#"+parameters))/*,
                Some(analysisResult.get._1.evaluationId), analysisResult.get._2.defaultVisualPlugin))*/
        } else {
            NotFound("The embedded analysis result does not exist.")
        }
    }

    def update(uriHash: String, id: String) = authenticated {user =>

        try
        {
            val storedResult = user.availableAnalysesResults.find{stored => stored.id == id}
            //val embedded = Payola.model.embeddingDescriptionModel.getEmbeddedAnalysisResult(uriHash)
            if (storedResult.isDefined) {
                Redirect(routes.Analysis.detail(
                    storedResult.get.analysisId+"#"+UriHashTools.embeddingUpdateParameter+"="+
                        storedResult.get.embeddingDescription.get.uriHash)).flashing(
                    "success" -> "The analysis result has been successfully updated.")
            }
            else {
                NotFound("The analysis result does not exist.")
            }
        }
        catch {
            case validationExc: ValidationException =>
                Redirect(routes.CacheStore.list()).flashing("error" -> validationExc.message)
        }
    }
}