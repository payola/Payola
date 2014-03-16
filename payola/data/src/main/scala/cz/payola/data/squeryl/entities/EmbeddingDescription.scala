package cz.payola.data.squeryl.entities

import cz.payola.data.squeryl._

object EmbeddingDescription extends EntityConverter[EmbeddingDescription]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[EmbeddingDescription] = {
        entity match {
            case e: EmbeddingDescription => Some(e)
            case e: cz.payola.common.entities.EmbeddingDescription =>
                Some(new EmbeddingDescription(e.owner.map(User(_)), e.uriHash, e.defaultVisualPlugin,
                    e.analysisResultId, new java.sql.Timestamp(e.lastUpdate.getTime())))
            case _ => None
        }
    }
}

class EmbeddingDescription (o: Option[User], uriH: String, defVisPlugin: Option[String],
    aResulId: String, lUpdate: java.sql.Timestamp)
    (implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.EmbeddingDescription(o, uriH, defVisPlugin, aResulId, lUpdate)
    with Entity with OptionallyOwnedEntity
{
}