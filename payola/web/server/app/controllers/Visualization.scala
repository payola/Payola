package controllers

import s2js.runtime.shared.DependencyProvider
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import views._
import helpers.Secured
import cz.payola.domain.entities.User
import cz.payola.web.shared.Payola
import cz.payola.web.shared.managers.PasswordManager
import cz.payola.domain.IDGenerator
import cz.payola.common.ValidationException


object Visualization extends PayolaController with Secured
{

    def datacube(evaluationId: String) = maybeAuthenticated {user =>
        val analysisId = Payola.model.analysisResultStorageModel.analysisId(evaluationId)
        Payola.model.analysisModel.getAccessibleToUserById(user, analysisId).map{ a =>
            Ok(views.html.visualization.datacube(user, analysisId, evaluationId))
        }.getOrElse{ throw new Exception("") }
    }

}