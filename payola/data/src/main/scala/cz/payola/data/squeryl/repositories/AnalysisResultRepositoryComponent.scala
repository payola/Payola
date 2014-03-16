package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities.AnalysisResult

import org.squeryl.PrimitiveTypeMode._
import s2js.runtime.shared.rpc.RpcException

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
        def storeResult(analysisDescription: cz.payola.domain.entities.AnalysisResult) {
            val converted = entityConverter(analysisDescription)
            wrapInTransaction{
                persist(converted)
            }
        }

        def getResult(evaluationId: String, analysisId: String): Option[AnalysisResult] = {
            selectOneWhere(anRes => anRes.analysisId === analysisId and anRes.evaluationId === evaluationId)
        }

        def deleteResult(evaluationId: String, analysisId: String) {
            wrapInTransaction{
                table.deleteWhere(anRes => anRes.analysisId === analysisId and anRes.evaluationId === evaluationId)
            }
        }

        def updateTimestamp(evaluationId: String) {
            wrapInTransaction{
                table.update(anRes =>
                    where(anRes.evaluationId === evaluationId)
                    set(anRes.touched := new java.sql.Timestamp(System.currentTimeMillis))
                )
            }
        }

        def exists(evaluationId: String): Boolean = {
            selectOneWhere(anRes => anRes.evaluationId === evaluationId).isDefined
        }

        def byEvaluationId(evaluationId: String): Option[AnalysisResult] = {
            selectOneWhere(anRes => anRes.evaluationId === evaluationId)
        }
    }
}
