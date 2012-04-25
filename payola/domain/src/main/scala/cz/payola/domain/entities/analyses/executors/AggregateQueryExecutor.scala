package cz.payola.domain.entities.analyses.executors

import cz.payola.domain.entities.analyses.{QueryExecution, QueryExecutor}

class AggregateQueryExecutor(private val subExecutors: Seq[QueryExecutor]) extends QueryExecutor
{
    def executeQuery(query: String, execution: QueryExecution): Int = {
        subExecutors.map(_.executeQuery(query, execution)).fold(0)(_ + _)
    }
}
