package cz.payola.domain.sparql.evaluation

import cz.payola.common.rdf.Graph

/**
 * Result of a query.
 */
abstract class QueryResult

/**
 * An error result that is returned when a fatal error occurs while performing a query.
 * @param error The fatal error object.
 */
case class ErrorResult(error: Throwable) extends QueryResult

/**
 * A success result that is returned when a datasource returns a graph without errors.
 * @param outputGraph The output graph returned by the query.
 */
case class SuccessResult(outputGraph: Option[Graph]) extends QueryResult

/**
 * A result meaning that the datasource hasn't returned any result in the specified time limit.
 */
object TimeoutResult extends QueryResult

/**
 * A result meaning that the query processing has been stopped.
 */
object StoppedResult extends QueryResult

/**
 * A result meaning that an error occured during evaluation.
 */
object ErrorResult extends QueryResult
