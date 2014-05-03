package cz.payola.web.shared

import s2js.compiler._
import cz.payola.model.ModelException
import cz.payola.domain.entities._
import cz.payola.web.shared.managers._
import cz.payola.common._
import cz.payola.domain.rdf.Graph
import cz.payola.common.EvaluationSuccess

@remote
@secured object AnalysisRunner
    extends ShareableEntityManager[Analysis, cz.payola.common.entities.Analysis](Payola.model.analysisModel)
{
    @async def runAnalysisById(id: String, oldEvaluationId: String, user: Option[User] = None)
        (successCallback: (String => Unit))
        (failCallback: (Throwable => Unit)) {

        val analysis = getAnalysisById(user, id)
        val evaluationId = Payola.model.analysisModel.run(analysis, oldEvaluationId, user)

        successCallback(evaluationId)
    }

    @async def getEvaluationState(evaluationId: String, analysisId: String, embeddedHash: String, user: Option[User] = None)
        (successCallback: (EvaluationState => Unit))
        (failCallback: (Throwable => Unit)) {

        val resultResponse =
            try{
                val response = Payola.model.analysisModel.getEvaluationState(evaluationId, user)
                response match {
                    case r: EvaluationSuccess =>
                        Payola.model.analysisResultStorageModel.saveGraph(
                            r.outputGraph, analysisId, evaluationId, user, if(embeddedHash == "") None else Some(embeddedHash))
                        val availableTransformators: List[String] =
                            TransformationManager.getAvailableTransformations(r.outputGraph)
                        EvaluationCompleted(availableTransformators, r.instanceErrors)

                    case _ =>
                        response
                }
            } catch {
                case e: ModelException => // the evaluation was never started, the result is in resultStorage
                    val graph = Payola.model.analysisResultStorageModel.getGraph(evaluationId)
                    val availableTransformators: List[String] = TransformationManager.getAvailableTransformations(graph)
                    EvaluationCompleted(availableTransformators, List())
                case p =>  {
                    throw p
                }
            }

        successCallback(resultResponse)
    }

    @async def evaluationExists(evaluationId: String, user: Option[User] = None)(successCallback: (Boolean => Unit))
        (failCallback: (Throwable => Unit)) {

        successCallback(Payola.model.analysisResultStorageModel.exists(evaluationId))
    }

    @async def embeddingExists(embedId: String, user: Option[User] = None)(successCallback: (Boolean => Unit))
        (failCallback: (Throwable => Unit)) {

        successCallback(Payola.model.embeddingDescriptionModel.exists(embedId))
    }


    /**
     * Partial analysis remote proxy
     * @param analysisId Analysis to make partial from
     * @param pluginInstanceId Plugin instance which makes the cutting point of the analysis
     * @param limitCount Limit plugin parameter value
     * @param user owner
     * @param successCallback
     * @param failCallback
     * @return
     * @author Jiri Helmich
     */
    @async def createPartialAnalysis(analysisId: String, pluginInstanceId: String, limitCount: Int, user: Option[User] = None)
        (successCallback: (String => Unit))
        (failCallback: (Throwable => Unit)) {
        val analysis = getAnalysisById(user, analysisId)
        val partialAnalysisId = Payola.model.analysisModel.makePartial(analysis, pluginInstanceId, limitCount)

        if (partialAnalysisId.isDefined){
            successCallback(partialAnalysisId.get)
        }
    }

    private def getAnalysisById(user: Option[User], id: String): Analysis = {
        Payola.model.analysisModel.getAccessibleToUserById(user, id).getOrElse {
            throw new ModelException("The analysis doesn't exist.")
        }
    }
}
