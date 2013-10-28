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
                    e.verticesCount, e.touched))
            case _ => None
        }
    }
}

class AnalysisResult (AnalysisID: String, o: Option[User], EvaluationID: String, Persist: Boolean,
    VerticesCount: Int, Touched: java.util.Date)
    (implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.AnalysisResult(AnalysisID, o, EvaluationID, Persist, VerticesCount, Touched)
    with Entity with OptionallyOwnedEntity
{ }
