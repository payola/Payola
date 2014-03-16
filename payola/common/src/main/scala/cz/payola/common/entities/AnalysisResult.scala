package cz.payola.common.entities

import cz.payola.common.Entity

trait AnalysisResult extends Entity with OptionallyOwnedEntity with NamedEntity {

    protected type EmbeddingDescriptionType <: EmbeddingDescription
    protected type AnalysisType <: Analysis

    protected var verticescount: Int
    protected var analysisid: String
    protected var evaluationid: String
    protected var _analysis: Option[AnalysisType]
    protected var _embeddingDescription: Option[EmbeddingDescriptionType]

    def touched: java.util.Date = null

    def touched_=(value: java.util.Date) {}

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

    /** Name of the analysis that this is result of*/
    def analysis: Option[AnalysisType] = _analysis

    def embeddingDescription: Option[EmbeddingDescriptionType] = _embeddingDescription
}
