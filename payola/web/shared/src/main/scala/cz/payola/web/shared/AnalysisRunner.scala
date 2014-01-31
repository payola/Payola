package cz.payola.web.shared

import s2js.compiler._
import cz.payola.model.ModelException
import cz.payola.domain.entities._
import cz.payola.web.shared.managers.ShareableEntityManager
import cz.payola.common._

@remote
@secured object  AnalysisRunner
    extends ShareableEntityManager[Analysis, cz.payola.common.entities.Analysis](Payola.model.analysisModel)
{
    @async def runAnalysisById(id: String, oldEvaluationId: String,
        checkAnalysisStore: Boolean = false, user: Option[User] = None)
        (successCallback: (String => Unit))
        (failCallback: (Throwable => Unit)) {

        val analysis = getAnalysisById(user, id)
        val evaluationId = Payola.model.analysisModel.run(analysis, oldEvaluationId, user)

        successCallback(evaluationId)
    }

    @async def getEvaluationState(evaluationId: String, analysisId: String, paginate: Boolean = false, user: Option[User] = None)
        (successCallback: (EvaluationState => Unit))
        (failCallback: (Throwable => Unit)) {

        //val host = "live.payola.cz"
        val host = "localhost:9000"

        val resultResponse =
            try{
                val response = Payola.model.analysisModel.getEvaluationState(evaluationId, user)
                response match {
                    case r: EvaluationSuccess =>
                            Payola.model.analysisResultStorageModel.saveGraph(r.outputGraph, analysisId, evaluationId, host, user)
                            EvaluationSuccess(Payola.model.analysisResultStorageModel.paginate(r.outputGraph),r.instanceErrors)

                    case _ => response
                }
            } catch {
                // the evaluation was never started, the result is in resultStorage
                case e: ModelException =>
                    user.map{ u =>
                        val graph = Payola.model.analysisResultStorageModel.getGraph(evaluationId)
                        EvaluationSuccess(if (paginate) { Payola.model.analysisResultStorageModel.paginate(graph) } else { graph }, List())
                    }.getOrElse { throw e }

                case e => throw e
            }

        successCallback(resultResponse)
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
