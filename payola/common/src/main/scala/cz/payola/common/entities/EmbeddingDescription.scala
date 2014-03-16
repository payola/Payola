package cz.payola.common.entities

import cz.payola.common.Entity

trait EmbeddingDescription extends Entity with OptionallyOwnedEntity with NamedEntity {
    protected var urihash: String
    protected var defaultvisualplugin: Option[String]
    protected var analysisresultid: String
    protected var evaluationid: String = ""

    def lastUpdate: java.util.Date = null

    def lastUpdate_=(value: java.util.Date) {}

    def uriHash = urihash

    def uriHash_=(value: String) {
        urihash = value
    }

    def defaultVisualPlugin = defaultvisualplugin

    def defaultVisualPlugin_=(value: Option[String]) {
        defaultvisualplugin = value
    }

    def analysisResultId = analysisresultid

    def analysisResultId_=(value: String) {
        analysisresultid = value
    }

    def evaluationId = evaluationid
}
