package cz.payola.domain.entities.analyses.messages

import cz.payola.domain.entities.DataSource

case class QueryExecutionProgress(successResults: Seq[DataSourceQuerySuccess], errorResults: Seq[DataSourceQueryError],
    unfinishedDataSources: Seq[DataSource])
{
    def isFinished: Boolean = unfinishedDataSources.isEmpty

    def value: Double = {
        if (unfinishedDataSources.isEmpty) {
            1
        } else {
            (successResults.length + errorResults.length) / unfinishedDataSources.length
        }
    }
}
