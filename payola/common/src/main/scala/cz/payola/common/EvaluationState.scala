package cz.payola.common

import cz.payola.common.rdf.Graph
import scala.collection.immutable
import cz.payola.common.entities.plugins.PluginInstance

/**
 * Result of an analysis evaluation.
 */
abstract class EvaluationState

case class EvaluationInProgress(value: Double, evaluatedInstances: immutable.Seq[PluginInstance],
    runningInstances: Seq[(PluginInstance, Double)],
    errors: Seq[(PluginInstance, String)]) extends EvaluationState

/**
 * An error result that is returned when a fatal error occurs during the analysis evaluation.
 * @param error The fatal error object.
 * @param instanceErrors The plugin instances, whose evaluations caused errors with the error objects.
 */
case class EvaluationError(error: String, instanceErrors: Seq[(PluginInstance, String)]) extends EvaluationState

/**
 * A success result that is returned when the output plugin instance returns a graph without errors. It's possible to
 * return the success result even though some plugin instance evaluations may have caused errors.
 * @param outputGraph The output graph returned by the output plugin instance.
 * @param instanceErrors The plugin instances, whose evaluations caused errors with the error objects.
 */
case class EvaluationSuccess(outputGraph: Graph, instanceErrors: Seq[(PluginInstance, String)], string: String = "") extends EvaluationState

/**
 * A success result that is returned when the output plugin instance returns a graph without errors. It's possible to
 * return the success result even though some plugin instance evaluations may have caused errors.
 * @param availableVisualTransformators List of graph transformators that can be used to process the result graph stored
 *                                      in cache
 * @param instanceErrors The plugin instances, whose evaluations caused errors with the error objects.
 */
case class EvaluationCompleted(availableVisualTransformators: List[String], instanceErrors: Seq[(PluginInstance, String)]) extends EvaluationState

/**
 * A result meaning that the analysis evaluation hasn't finished in the specified time limit.
 */
class EvaluationTimeout extends EvaluationState

/**
 * A result meaning that the analysis evaluation has been stopped.
 */
class EvaluationStopped extends EvaluationState
