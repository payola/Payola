package cz.payola.data.squeryl.entities

import cz.payola.data.squeryl._
import scala.Some

object AnalysisResult extends EntityConverter[AnalysisResult]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[AnalysisResult] = {
        entity match {
            case e: AnalysisResult => Some(e)
            case e: cz.payola.common.entities.AnalysisResult =>
                Some(new AnalysisResult(e.analysisId, e.owner.map(User(_)), e.evaluationId, e.storedIn,
                    e.userId, e.verticesCount, e.touchedTime))
            case _ => None
        }
    }
}

class AnalysisResult (val AnalysisID: String, val o: Option[User], val EvaluationID: String, val Persist: Boolean,
    val UserID: String, val VerticesCount: Int,
    val Touched: java.util.Date)(implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.AnalysisResult(AnalysisID, o, EvaluationID, Persist, UserID, VerticesCount, Touched)
    with Entity with OptionallyOwnedEntity
{
    evaluationid = EvaluationID
    stored = Persist
    analysisid = AnalysisID
    userid = if(_owner.isDefined) _owner.get.id else UserID
    verticescount = VerticesCount
    touchedtime = Touched
}
