package cz.payola.common.entities

import cz.payola.common.Entity

trait AnalysisResult extends Entity with OptionallyOwnedEntity with NamedEntity {
    protected var verticescount: Int
    protected var analysisid: String
    protected var evaluationid: String
    protected var _persist: Boolean

    def storedIn = persist

    def storedIn_=(value: Boolean) {
        persist = value
    }

    def touched: java.util.Date = null

    def touched_=(value: java.util.Date) {}

    def persist = _persist

    def persist_=(value: Boolean) {
        _persist = value
    }

    def evaluationId = evaluationid

    def evaluationId_=(value: String) {
        evaluationid = value
    }

    def verticesCount = verticescount

    def  verticesCount_=(value: Int) {
        verticescount = value
    }

    def analysisId = analysisid

    def analysisId_=(value: String) {
        analysisid = value
    }
}
