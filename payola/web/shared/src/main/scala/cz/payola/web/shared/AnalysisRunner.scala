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
    val runningEvaluations: HashMap[String, (Option[User], AnalysisEvaluation, Long)] = new
            HashMap[String, (Option[User], AnalysisEvaluation, Long)]

    @async def runAnalysisById(id: String, timeoutSeconds: Long, oldEvaluationId: String, user: Option[User] = None)
        (successCallback: (String => Unit))
        (failCallback: (Throwable => Unit)) {
        val analysis = Payola.model.analysisModel.getAccessibleToUserById(user, id).getOrElse {
            throw new ModelException("The analysis doesn't exist.") // TODO
        }

        if (runningEvaluations.isDefinedAt(oldEvaluationId)) {
            if (!runningEvaluations.get(oldEvaluationId).filter(_._2.analysis.id == id).isEmpty) {
                runningEvaluations.remove(oldEvaluationId)
            }
        }

        val evaluationId = IDGenerator.newId
        val timeout = scala.math.min(1800, timeoutSeconds)
        runningEvaluations
            .put(evaluationId, (user, analysis.evaluate(Some(timeout * 10)), (new java.util.Date).getTime))

        successCallback(evaluationId)
    }

    private def getEvaluationTupleForID(id: String) = {
        val date = new java.util.Date
        runningEvaluations.foreach { tuple =>
            if (tuple._2._3 + (20 * 60 * 1000) < date.getTime) {
                runningEvaluations.remove(tuple._1)
            }
        }

        runningEvaluations.get(id).getOrElse {
            throw new ModelException("The evaluation is not running.")
        }
    }

    private def getEvaluationTupleForIDAndPerformSecurityChecks(id: String, user: Option[User]) = {
        val evaluationTuple = getEvaluationTupleForID(id)
        if (!evaluationTuple._1.isDefined || evaluationTuple._1 == user) {
            evaluationTuple
        } else {
            throw new ModelException("Forbidden evaluation.")
        }
    }

    @async def downloadAnalysisResultAsXML(evaluationId: String, user: Option[User] = None)
        (successCallback: (() => Unit))(failCallback: (Throwable => Unit)) {
        val evaluationTuple = getEvaluationTupleForIDAndPerformSecurityChecks(evaluationId, user)

        val evaluation = evaluationTuple._2
        if (!evaluation.isFinished) {
            failCallback(new ModelException("Evaluation isn't finished yet."))
        } else {
            successCallback()
        }
    }

    @async def getEvaluationState(evaluationId: String, user: Option[User] = None)
        (successCallback: (EvaluationState => Unit))(failCallback: (Throwable => Unit)) {
        val evaluationTuple = getEvaluationTupleForIDAndPerformSecurityChecks(evaluationId, user)

        runningEvaluations.put(evaluationId, (evaluationTuple._1, evaluationTuple._2, (new java.util.Date).getTime))

        val evaluation = evaluationTuple._2

        val response = evaluation.getResult.map {
            case r: Error => EvaluationError(transformException(r.error),
                r.instanceErrors.toList.map { e => (e._1, transformException(e._2))})
            case r: Success => EvaluationSuccess(r.outputGraph,
                r.instanceErrors.toList.map { e => (e._1, transformException(e._2))})
            case Timeout => new EvaluationTimeout
            case _ => throw new Exception("Unhandled evaluation state")
        }.getOrElse {
            val progress = evaluation.getProgress
            EvaluationInProgress(progress.value, progress.evaluatedInstances, progress.runningInstances.toList,
                progress.errors.toList.map { e => (e._1, transformException(e._2))})
        }

        successCallback(response)
    }

    private def transformException(t: Throwable): String = {
        t match {
            case e: Exception => e.getMessage
            case _ => "Unknown error."
        }
    }
}
