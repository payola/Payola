package cz.payola.data.rdf

object QueryResult
{
    def empty = QueryResult(Nil, Nil, 0)
}

case class QueryResult(data: List[String], errors: List[Throwable], expectedResultCount: Int)
