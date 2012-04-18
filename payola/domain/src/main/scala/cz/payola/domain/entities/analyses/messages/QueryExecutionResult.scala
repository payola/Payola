package cz.payola.domain.entities.analyses.messages

import scala.collection

object QueryExecutionResult
{
    def empty = QueryExecutionResult(Nil)
}

case class QueryExecutionResult(data: collection.Seq[String])
