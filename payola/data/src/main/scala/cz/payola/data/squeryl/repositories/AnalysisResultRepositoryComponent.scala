package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities.AnalysisResult

import org.squeryl.PrimitiveTypeMode._

trait AnalysisResultRepositoryComponent extends TableRepositoryComponent {
    self: SquerylDataContextComponent =>

    /**
     * A repository to access persisted results of analyses
     */
    lazy val analysisResultRepository = new AnalysisResultDefaultTableRepository

    class AnalysisResultDefaultTableRepository
        extends OptionallyOwnedEntityDefaultTableRepository[AnalysisResult](schema.analysesResults, AnalysisResult)
        with AnalysisResultRepository
    {
        def storeAnalysis(analysisDescription: cz.payola.domain.entities.AnalysisResult) {
            val converted = entityConverter(analysisDescription)
            table.insert(converted)
        }

        def getNumberOfStoredAnalyses(): Long = {
            selectWhere(_.Persist === false).size
        }

        def getNumberOfStoredForUser(userId: String): Long = {
            selectWhere(_.UserID === userId).size
        }

        def getStoredAnalysis(userId: String, analysisId: String): Option[AnalysisResult] = {
            selectOneWhere(anRes => anRes.AnalysisID === analysisId and anRes.UserID === userId)
        }

        def deleteStoredAnalysis(userId: String, analysisId: String) {
            table.deleteWhere(anRes => anRes.AnalysisID === analysisId and anRes.UserID === userId)
        }

        def updateTimestamp(userId: String, analysisId: String) {
            table.update(anRes =>
                where(anRes.AnalysisID === analysisId and anRes.UserID === userId)
                set(anRes.Touched := new java.util.Date(System.currentTimeMillis))
            )
        }

        def getEvaluationId(userId: String, analysisId: String): Option[String] = {
            val result = selectWhere(sel => sel.UserID === userId and sel.AnalysisID === analysisId)
            if(result.isEmpty) {
                None
            } else {
                Some(result.head.evaluationId)
            }
        }

        def deleteOldest() {
            val minTime: Option[java.util.Date] =
                from(table)(a => where(a.Persist === true) compute(min(a.Touched)))

            if(minTime.isDefined) {
                table.deleteWhere(anRes =>
                    anRes.Touched === minTime.get and anRes.Persist === true
                )
            }
        }

        def deleteOldest(userId: String) {
            val minTime: Option[java.util.Date] =
                from(table)(a => where(a.UserID === userId and a.Persist === true) compute(min(a.Touched)))

            if(minTime.isDefined) {
                table.deleteWhere(anRes =>
                    anRes.UserID === userId and anRes.Touched === minTime.get and anRes.Persist === true
                )
            }
        }
    }
}
