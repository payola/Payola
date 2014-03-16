package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities._
import cz.payola.data.squeryl.TableRepositoryComponent
import cz.payola.common.entities.AnalysisResult
import cz.payola.domain._

import org.squeryl.PrimitiveTypeMode._

trait EmbeddingDescriptionRepositoryComponent extends TableRepositoryComponent {
    self: SquerylDataContextComponent =>

    lazy val embeddingDescriptionRepository = new EmbeddingDescriptorDefaultTableRepository

    class EmbeddingDescriptorDefaultTableRepository
        extends OptionallyOwnedEntityDefaultTableRepository[EmbeddingDescription](schema.embeddingDescription, EmbeddingDescription)
        with EmbeddingDescriptionRepository {

        def createEmbeddedUriHash(analysisResult: AnalysisResult): EmbeddingDescription = {
            wrapInTransaction{
                val embeddedUri = new EmbeddingDescription(analysisResult.owner.map(User(_)),
                    IDGenerator.newId, None, analysisResult.id, new java.sql.Timestamp(System.currentTimeMillis))

                persist(embeddedUri)
                embeddedUri
            }
        }

        def getEmbeddedUriHash(analysisResultId: String): Option[EmbeddingDescription] = {
            selectOneWhere(ed => ed.analysisResultId === analysisResultId)
        }

        def getAllAvailableToUser(userId: Option[String]): Seq[EmbeddingDescription] = {
            selectWhere(p => p.ownerId === userId)
        }

        def getEmbeddedById(embedId: String): Option[EmbeddingDescription] = {
            selectOneWhere(ed => ed.id === embedId)
        }

        def getEmbeddedByUriHash(uriHash: String): Option[EmbeddingDescription] = {
            selectOneWhere(ed => ed.uriHash === uriHash)
        }

        def removeByAnalysisId(id: String): Boolean = wrapInTransaction {
            table.deleteWhere(e => id === e.analysisResultId) == 1
        }

        def setViewPlugin(id: String, visualPlugin: String): Option[EmbeddingDescription] = {
            val embeddingDescription = getById(id)
            if(embeddingDescription.isDefined) {
                embeddingDescription.get.defaultVisualPlugin = Some(visualPlugin)
                persist(embeddingDescription.get)
            }

            embeddingDescription
        }

        def updateEvaluation(uriHash: String, analysisResultId: String) {
            val embeddingDescription = getEmbeddedByUriHash(uriHash)
            if(embeddingDescription.isDefined) {
                embeddingDescription.get.analysisResultId = analysisResultId
                persist(embeddingDescription.get)
            }
        }
    }
}
