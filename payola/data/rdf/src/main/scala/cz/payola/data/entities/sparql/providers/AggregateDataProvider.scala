package cz.payola.data.sparql.providers

import cz.payola.data.sparql.{QueryExecutor, DataProvider}

class AggregateDataProvider(val subProviders: List[DataProvider]) extends DataProvider
{
    def executeQuery(query: String, executor: QueryExecutor): Int = {
        subProviders.map(_.executeQuery(query, executor)).sum
    }
}
