package controllers

import helpers.Secured
import play.api.mvc._
import cz.payola.web.shared.Payola

object DataCube extends PayolaController with Secured
{
    def findDataStructureDefinitions(evaluationId: String) = Action {
        Ok(Payola.model.dataCubeModel.queryForCubeDefinitions(evaluationId))
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

    def distinctValues(evaluationId: String, property: String, isDate: Boolean) = Action {
        Ok(Payola.model.dataCubeModel.distinctValues(evaluationId, property, isDate))
    }

    def filteredData(evaluationId: String, measure: String, dimension: String, filters: List[String]) = Action {
        Ok(Payola.model.dataCubeModel.dataset(evaluationId, measure, dimension, filters))
    }
}