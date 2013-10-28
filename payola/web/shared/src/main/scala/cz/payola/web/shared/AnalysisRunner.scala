package cz.payola.web.shared

import s2js.compiler._
import cz.payola.model.ModelException
import cz.payola.domain.entities._
import cz.payola.web.shared.managers.ShareableEntityManager
import cz.payola.common._

@remote
@secured object AnalysisRunner
    extends ShareableEntityManager[Analysis, cz.payola.common.entities.Analysis](Payola.model.analysisModel)
{
    @async def runAnalysisById(id: String, timeoutSeconds: Long, oldEvaluationId: String,
        checkAnalysisStore: Boolean = false, user: Option[User] = None)
        (successCallback: (String => Unit))
        (failCallback: (Throwable => Unit)) {

        val analysis = getAnalysisById(user, id)
        val evaluationId = Payola.model.analysisModel.run(analysis, timeoutSeconds, oldEvaluationId, user)

        successCallback(evaluationId)
    }

    @async def getEvaluationState(evaluationId: String, analysisId: String,
        storeAnalysis: Boolean = false, persistInAnalysisStorage: Boolean = false, paginate: Boolean = false,
        user: Option[User] = None)
        (successCallback: (EvaluationState => Unit))
        (failCallback: (Throwable => Unit)) {

        val resultResponse =
            try{
                val response = Payola.model.analysisModel.getEvaluationState(evaluationId, user)

                //Console.println("Getting evaluation state.")
                if(storeAnalysis) {
                    response match {
                        case r: EvaluationSuccess =>
                            //Console.println("About to store analysis result.")
                            Payola.model.analysisResultStorageModel.saveGraph(
                                r.outputGraph, analysisId, evaluationId, persistInAnalysisStorage, user)
                            //Console.println("saved")
                            EvaluationSuccess(
                                Payola.model.analysisResultStorageModel.paginate(r.outputGraph),
                                r.instanceErrors)

                        case _ =>
                            //Console.println("2")
                            response
                    }
                } else {
                    //Console.println("3")
                    response
                }
            } catch {
                case e: ModelException => // the evaluation was never started, the result is in resultStorage
                    //Console.println("Error Occured.")
                    if(user.isDefined) {
                        //Console.println("About to load analysis result.")
                        val graph = Payola.model.analysisResultStorageModel.getGraph(evaluationId)

                        if(paginate) {
                            val paginated = Payola.model.analysisResultStorageModel.paginate(graph)
                            EvaluationSuccess(paginated, List())
                        } else {
                            EvaluationSuccess(graph, List())
                        }
                    } else {
                        throw e
                    }
                case p =>  {
                    //Console.println("Error Occured.")
                    throw p
                }
            }

        successCallback(resultResponse)
    }

    @async def createPartialAnalysis(analysisId: String, pluginInstanceId: String, user: Option[User] = None)
        (successCallback: (String => Unit))
        (failCallback: (Throwable => Unit)) {
        val analysis = getAnalysisById(user, analysisId)
        val partialAnalysisId = Payola.model.analysisModel.makePartial(analysis, pluginInstanceId)

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
