package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities._

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.ast.LogicalBoolean
import s2js.runtime.shared.rpc.RpcException

trait AnalysisResultRepositoryComponent extends TableRepositoryComponent {
    self: SquerylDataContextComponent =>
    private type QueryType = (AnalysisResult, Option[Analysis], Option[EmbeddingDescription])

    /**
     * A repository to access persisted results of analyses
     */
    lazy val analysisResultRepository =
        new TableRepository[AnalysisResult, QueryType](schema.analysesResults, AnalysisResult)
            with AnalysisResultRepository
            with NamedEntityTableRepository[AnalysisResult]
            with OptionallyOwnedEntityTableRepository[AnalysisResult, QueryType]
        {

            def storeResult(analysisDescription: cz.payola.domain.entities.AnalysisResult, uriHash: Option[String] = None) {
                val converted = entityConverter(analysisDescription)
                wrapInTransaction{
                    persist(converted)
                    uriHash.foreach{ uri =>
                        val embedded = embeddingDescriptionRepository.getEmbeddedByUriHash(uri)
                        if(embedded.isDefined) {
                            val oldAnalysisResultId = embedded.get.analysisResultId
                            embedded.get.analysisResultId = converted.id
                            embeddingDescriptionRepository.persist(embedded.get)
                            removeById(oldAnalysisResultId)
                        }
                    }
                }
            }

            def getResult(evaluationId: String, analysisId: String): Option[AnalysisResult] = {
                getOneAnalysisResult(anRes => anRes.analysisId === analysisId and anRes.evaluationId === evaluationId)
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

            def byEvaluationId(evaluationId: String): Option[AnalysisResult] = {
                selectOneWhere(anRes => anRes.evaluationId === evaluationId)
            }

            def exists(evaluationId: String): Boolean = {
                getOneAnalysisResult(anRes => anRes.evaluationId === evaluationId).isDefined
            }

            private def getOneAnalysisResult(entityFilter: AnalysisResult => LogicalBoolean): Option[AnalysisResult] = {
                val data = selectWhere(entityFilter)
                if(data.isEmpty)
                    None
                else
                    Some(data.head)
            }

            def getAllAvailableToUser(userId: Option[String]): Seq[AnalysisResult] = {
                selectWhere(p => p.ownerId === userId)
            }

            protected def getSelectQuery(entityFilter: (AnalysisResult) => LogicalBoolean) = {
                join(table, schema.analyses.leftOuter, schema.embeddingDescription.leftOuter)((result, anal, emb) =>
                    where(entityFilter(result))
                        select(result, anal, emb)
                        on(Some(result.analysisId) === anal.map(_.id),
                        emb.map(_.analysisResultId) === Some(result.id))
                )
            }

            protected def processSelectResults(results: Seq[QueryType]) = {
                wrapInTransaction {
                    results.map {r =>
                        val anResult = r._1
                        anResult.analysis = r._2
                        anResult.embeddingDescription = r._3
                        anResult
                    }(collection.breakOut)
                }
            }
        }
}
