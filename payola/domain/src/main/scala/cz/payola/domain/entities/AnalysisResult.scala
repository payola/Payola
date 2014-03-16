package cz.payola.domain.entities

import cz.payola.domain._
import java.sql.Timestamp

class AnalysisResult(
    protected override var analysisid: String,
    protected override var _owner: Option[User],
    protected override var evaluationid: String,
    protected override var verticescount: Int,
    protected var _touched: Timestamp)
    extends Entity
    with OptionallyOwnedEntity
    with NamedEntity
    with cz.payola.common.entities.AnalysisResult
{
    type EmbeddingDescriptionType = EmbeddingDescription
    type AnalysisType = Analysis

    protected override var _analysis: Option[Analysis] = None

    protected override var _embeddingDescription: Option[EmbeddingDescription] = None


    def analysis_=(value: Option[Analysis]) {
        _analysis = value
    }

    def embeddingDescription_=(value: Option[EmbeddingDescription]) {
        _embeddingDescription = value
    }

    override var _name = "http://"+analysisid+"/"+evaluationid

    override def name = "http://"+analysisid+"/"+evaluationid

    override def name_=(value: String) {
        //ain't gonna do a thing
    }

    override def touched = new java.util.Date(_touched.getTime())

    override def touched_=(value: java.util.Date) {
        _touched = new java.sql.Timestamp(value.getTime)
    }

    /**
     * Sets the owner of the analysisResult.
     * @param value The new owner of the analysisResult.
     */
    override def owner_=(value: Option[UserType]) {
        _owner = value
        super[OptionallyOwnedEntity].checkInvariants()
    }

    override final def canEqual(other: Any): Boolean = {
        other.isInstanceOf[AnalysisResult]
    }

    override protected final def checkInvariants() {
        super[Entity].checkInvariants()

        validate(analysisid.length > 0, "analysisid", "Analysisid has to be specified")
        validate(evaluationid.length > 0, "evaluationid", "Evaluationid has to be specified")
    }
}
