package controllers

import helpers.Secured
import play.api.mvc._
import cz.payola.web.shared.Payola

object DataCube extends PayolaController with Secured
{
    def findDataStructureDefinitions(evaluationId: String) = Action {
        Ok(Payola.model.dataCubeModel.queryForCubeDSD(evaluationId))
    }

    def listDimensions(evaluationId: String, dsdUri: String) = Action {
        Ok(Payola.model.dataCubeModel.listComponents(evaluationId, dsdUri, "dimension"))
    }

    def listAttributes(evaluationId: String, dsdUri: String) = Action {
        Ok(Payola.model.dataCubeModel.listComponents(evaluationId, dsdUri, "attribute"))
    }

    def listMeasures(evaluationId: String, dsdUri: String) = Action {
        Ok(Payola.model.dataCubeModel.listComponents(evaluationId, dsdUri, "measure"))
    }
}