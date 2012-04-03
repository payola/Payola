package cz.payola.data.rdf.providers

import cz.payola.data.rdf.{DataProvider, QueryExecutor}

class AggregateDataProvider(val subProviders: Seq[DataProvider]) extends DataProvider
{
    def executeQuery(query: String, executor: QueryExecutor): Int = {
        subProviders.map(_.executeQuery(query, executor)).sum
    }
}
