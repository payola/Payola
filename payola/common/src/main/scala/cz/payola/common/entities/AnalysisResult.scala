package cz.payola.common.entities

import cz.payola.common.Entity

trait AnalysisResult extends Entity with OptionallyOwnedEntity with NamedEntity {
    protected var touchedtime: java.util.Date
    protected var verticescount: Int
    protected var analysisid: String
    protected var userid: String
    protected var evaluationid: String
    protected var stored: Boolean

    def storedIn = stored

    def storedIn_=(value: Boolean) {
        stored = value
    }

    def evaluationId = evaluationid

    def evaluationId_=(value: String) {
        evaluationid = value
    }

    def touchedTime = touchedtime

    def touchedTime_=(value: java.util.Date) {
        touchedtime = value
    }

    def verticesCount = verticescount

    def  verticesCount_=(value: Int) {
        verticescount = value
    }

    def analysisId = analysisid

    def analysisId_=(value: String) {
        analysisid = value
    }

    def userId = userid

    def userId_=(value: String) {
        userid = value
    }
}
