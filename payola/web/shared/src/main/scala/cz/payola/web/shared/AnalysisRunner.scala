package cz.payola.web.shared

import s2js.compiler._
import cz.payola.model.ModelException
import cz.payola.domain.entities._
import cz.payola.web.shared.managers.ShareableEntityManager
import cz.payola.common.EvaluationState

@remote
@secured object AnalysisRunner
    extends ShareableEntityManager[Analysis, cz.payola.common.entities.Analysis](Payola.model.analysisModel)
{
    @async def runAnalysisById(id: String, timeoutSeconds: Long, oldEvaluationId: String, user: Option[User] = None)
        (successCallback: (String => Unit))
        (failCallback: (Throwable => Unit)) {
        val analysis = getAnalysisById(user, id)
        val evaluationId = Payola.model.analysisModel.run(analysis, timeoutSeconds, oldEvaluationId, user)

        successCallback(evaluationId)
    }

    @async def getEvaluationState(evaluationId: String, user: Option[User] = None)
        (successCallback: (EvaluationState => Unit))
        (failCallback: (Throwable => Unit)) {
        val response = Payola.model.analysisModel.getEvaluationState(evaluationId, user)
        successCallback(response)
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
