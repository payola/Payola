package cz.payola.domain.entities

import cz.payola.domain.Entity

class AnalysisResult(
    protected override var analysisid: String,
    protected override var _owner: Option[User],
    protected override var evaluationid: String,
    protected override var stored: Boolean,
    protected override var userid: String,
    protected override var verticescount: Int,
    protected override var touchedtime: java.util.Date)
    extends Entity with cz.payola.common.entities.AnalysisResult
    with OptionallyOwnedEntity with NamedEntity
{

    override var _name = "http://"+userid+"/"+analysisid

    override def name = "http://"+userid+"/"+analysisid

    override def name_=(value: String) {
        //ain't gonna do a thing
    }

    /**
     * Sets the owner of the analysisResult and the userId.
     * @param value The new owner of the analysisResult.
     */
    override def owner_=(value: Option[UserType]) {
        _owner = value
        if(value.isDefined) userid = value.get.id
        super[OptionallyOwnedEntity].checkInvariants()
    }

    override final def canEqual(other: Any): Boolean = {
        other.isInstanceOf[AnalysisResult]
    }

    override protected final def checkInvariants() {
        super[Entity].checkInvariants()

        validate(analysisid.length > 0, "analysisid", "Analysisid has to be specified")
        validate(userid.length > 0, "userid", "Userid has to be specified")
    }
}
