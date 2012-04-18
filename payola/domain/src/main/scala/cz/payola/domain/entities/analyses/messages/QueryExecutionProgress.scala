package cz.payola.domain.entities.analyses.messages

import cz.payola.domain.entities.DataSource

case class QueryExecutionProgress(successDataSources: collection.Seq[DataSource],
    errorDataSources: collection.Seq[DataSource], unfinishedDataSources: collection.Seq[DataSource])
{
    def isFinished: Boolean = unfinishedDataSources.isEmpty

    def value: Double = {
        if (unfinishedDataSources.isEmpty) {
            1
        } else {
            (successDataSources.length + errorDataSources.length) / unfinishedDataSources.length
        }
    }
}
