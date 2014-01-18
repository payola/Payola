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
            //Console.println("inserting converted graph")
            wrapInTransaction{
                persist(converted)
            }
        }

        def getResultsCount(): Long = {
            selectWhere(_.persist === false).size
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

        def purge() {      //TODO make conditional and proper delete based on analysisResult.touched

            wrapInTransaction{
                table.deleteWhere(_.persist === false)
            }
            /*val qResult = select(from(table)(a => where(a.persist === true) compute(min(a.touched))))

            val minTime = if(qResult.isEmpty) None else Some(qResult(0).touched)

            if(minTime.isDefined) {
                wrapInTransaction{
                    table.deleteWhere(anRes =>
                        anRes.touched === minTime.get and anRes.persist === true
                    )
                }
            }*/
        }
    }
}
