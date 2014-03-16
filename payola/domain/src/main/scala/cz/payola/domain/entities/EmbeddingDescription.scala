package cz.payola.domain.entities

import cz.payola.domain.Entity
import java.sql.Timestamp

class EmbeddingDescription(
    protected override var _owner: Option[User],
    protected override var urihash: String,
    protected override var defaultvisualplugin: Option[String],
    protected override var analysisresultid: String,
    protected var lastupdate: Timestamp,
    protected override var _name: String = "")
    extends Entity with cz.payola.common.entities.EmbeddingDescription
    with OptionallyOwnedEntity with NamedEntity {

    override def lastUpdate = new java.util.Date(lastupdate.getTime())

    override def lastUpdate_=(value: java.util.Date) {
        lastupdate = new java.sql.Timestamp(value.getTime)
    }

    override final def canEqual(other: Any): Boolean = {
        other.isInstanceOf[EmbeddingDescription]
    }

    override protected final def checkInvariants() {
        super[Entity].checkInvariants()

        validate(analysisresultid.length > 0, "analysisresultid", "AnalysisResultId has to be specified")
        validate(urihash.length > 0, "urihash", "Urihash has to be specified")
    }
}
