package cz.payola.web.shared

import cz.payola.domain.entities.analyses.evaluation._
import scala.collection.mutable.HashMap
import s2js.compiler._
import cz.payola.model.ModelException
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.User
import cz.payola.domain.entities.analyses.evaluation.Success

// TODO move the logic to the model.
@remote
@secured object AnalysisRunner
{
    val runningEvaluations: HashMap[String, (Option[User], AnalysisEvaluation)] = new
            HashMap[String, (Option[User], AnalysisEvaluation)]

    @async def runAnalysisById(id: String, user: Option[User] = None)(successCallback: (String => Unit))
        (failCallback: (Throwable => Unit)) {
        val analysis = Payola.model.analysisModel.getAccessibleToUserById(user, id).getOrElse {
            throw new ModelException("The analysis doesn't exist.") // TODO
        }

        val evaluationId = IDGenerator.newId
        runningEvaluations.put(evaluationId, (user, analysis.evaluate()))

        successCallback(evaluationId)
    }


    private def getEvaluationTupleForID(id: String) = {
        runningEvaluations.get(id).getOrElse {
            throw new ModelException("The evaluation is not running.")
        }
    }

    private def getEvaluationTupleForIDAndPerformSecurityChecks(id: String, user: Option[User]) = {
        val evaluationTuple = getEvaluationTupleForID(id)
        if (!evaluationTuple._1.isDefined || evaluationTuple._1.get.equals(user)) {
            evaluationTuple
        }else{
            throw new ModelException("Forbidden evaluation.")
        }
    }

    @async def downloadAnalysisResultAsXML(evaluationId: String, user: Option[User] = None)
        (successCallback: (() => Unit))(failCallback: (Throwable => Unit)) = {
        val evaluationTuple = getEvaluationTupleForIDAndPerformSecurityChecks(evaluationId, user)

        val evaluation = evaluationTuple._2
        if (!evaluation.isFinished) {
            failCallback(new ModelException("Evaluation isn't finished yet."))
        }else{
            successCallback()
        }


    }

    @async def getAnalysisProgress(evaluationId: String, user: Option[User] = None)
        (successCallback: (AnalysisProgress => Unit))(failCallback: (Throwable => Unit)) = {
        val evaluationTuple = getEvaluationTupleForIDAndPerformSecurityChecks(evaluationId, user)

        val evaluation = evaluationTuple._2
        val progress = evaluation.getProgress

        val evaluated = progress.evaluatedInstances.map(i => i.id)
        val running = progress.runningInstances.map(m => m._1.id).toList
        val errors = progress.errors.map(tuple => tuple._1.id).toList

//        if (evaluation.isFinished) {
//            runningEvaluations -= evaluationId
//        }

        val graph = evaluation.getResult.flatMap {
            case r: Success => Some(r.outputGraph)
            case _ => None
        }

        successCallback(
            new AnalysisProgress(evaluated, running, errors, progress.value, evaluation.isFinished, graph))
    }
}
