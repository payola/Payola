package cz.payola.web.shared

import cz.payola.domain.entities.analyses.evaluation._
import cz.payola.data.entities.dao.FakeAnalysisDAO

/**
  *
  * @author jirihelmich
  * @created 5/24/12 1:44 AM
  * @package cz.payola.web.shared
  */

@remote object AnalysisRunner
{
    val runningEvaluations : Map[String, AnalysisEvaluation] = Map()

    def runAnalysisById(id: String) = {
        val evaluation = new AnalysisEvaluation(FakeAnalysisDAO.analysis, 5000)
        evaluation.act()

        runningEvaluations += ("id", evaluation)

        "id"
    }

    def getAnalysisProgress(evaluationId: String) : AnalysisProgress = {

        val evaluation = runningEvaluations.get(evaluationId).get
        val progress = evaluation.getProgress

        val evaluated = progress.evaluatedInstances.map(i => i.id)
        val running = progress.runningInstances.map(i => i.id)
        val errors = progress.errors.map(tuple => tuple._1.id)

        if (evaluation.isFinished)
        {
            runningEvaluations -= "id"
        }

        new AnalysisProgress(evaluated, running, errors, evaluation.isFinished)
    }
}
