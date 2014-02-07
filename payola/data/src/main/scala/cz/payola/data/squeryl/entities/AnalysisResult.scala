package cz.payola.data.squeryl.entities

import cz.payola.data.squeryl._
import java.sql.Timestamp

object AnalysisResult extends EntityConverter[AnalysisResult]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[AnalysisResult] = {
        entity match {
            case e: AnalysisResult => Some(e)
            case e: cz.payola.common.entities.AnalysisResult =>
                Some(new AnalysisResult(e.analysisId, e.owner.map(User(_)), e.evaluationId,
                    e.verticesCount, new Timestamp(e.touched.getTime())))
            case _ => None
        }
    }
}

class AnalysisResult (AnalysisID: String, o: Option[User], EvaluationID: String,
    VerticesCount: Int, Touched: java.sql.Timestamp)
    (implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.AnalysisResult(AnalysisID, o, EvaluationID, VerticesCount, Touched)
    with Entity with OptionallyOwnedEntity
{ }
