package cz.payola.domain.sparql.evaluation

/**
 * A message used to control launched query.
 */
abstract class QueryRunnerControl

/**
 * Signal to fetch result of the query.
 */
private object GetResult extends QueryRunnerControl

/**
 * Signal to terminate the query processing.
 */
private object Terminate extends QueryRunnerControl

/**
 * Signal to end query processing.
 */
private object Stop extends QueryRunnerControl