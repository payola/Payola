package cz.payola.web.shared

import cz.payola.domain.entities.analyses.evaluation._
import cz.payola.data.dao.AnalysisDAO
import scala.collection.mutable.HashMap

@remote
object AnalysisRunner
{
    val runningEvaluations : HashMap[String, AnalysisEvaluation] = new HashMap[String, AnalysisEvaluation]

    def runAnalysisById(id: String) = {
        //TODO: Get AnalysisDAO from datafacade! (JH)
        val analysisOpt = new AnalysisDAO().getById(id)

        if (analysisOpt.isEmpty) {
            throw new EntityNotFoundException
        }

        runningEvaluations.put(id, analysisOpt.get.evaluate())

        id
    }

    def getAnalysisProgress(evaluationId: String) : AnalysisProgress = {

        val evaluation = runningEvaluations.get(evaluationId).get
        val progress = evaluation.getProgress

        val evaluated = progress.evaluatedInstances.map(i => i.id)
        val running = progress.runningInstances.map(m => m._1.id).toList
        val errors = progress.errors.map(tuple => tuple._1.id).toList

        if (evaluation.isFinished)
        {
            runningEvaluations -= evaluationId
        }

        val graph = evaluation.getResult.flatMap{
            case r: Success => Some(r.outputGraph)
            case _ => None
        }

        new AnalysisProgress(evaluated, running, errors, progress.value, evaluation.isFinished, graph)
    }
}
