package cz.payola.domain.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.domain.entities.sources.SparqlEndpointDataSource
import scala.actors.Actor
import scala.collection.mutable.Queue
import cz.payola.domain.entities.{Analysis, DataSource}
import cz.payola.domain.entities.analyses.plugins.SparqlQuery
import cz.payola.domain.entities.analyses.messages._
import cz.payola.domain.entities.analyses._
import cz.payola.domain.rdf._

class AnalysisEvaluationSpecs extends FlatSpec with ShouldMatchers
{
    val dbPediaDataSource = new SparqlEndpointDataSource("DBPedia", None, "http://dbpedia.org/sparql")

    val dataGovDataSource = new SparqlEndpointDataSource("Data.gov", None, "http://services.data.gov/sparql")

    val invalidDataSource = new SparqlEndpointDataSource("Invalid", None, "http://invalid/sparql")

    val selectQuery = "select distinct ?Concept where {[] a ?Concept} LIMIT 100";

    val constructQuery = """
        CONSTRUCT {
            <http://dbpedia.org/resource/Prague> ?p1 ?n1 .
            ?n1 ?p2 ?n2 .
        }
        WHERE {
            <http://dbpedia.org/resource/Prague> ?p1 ?n1 .
            OPTIONAL { ?n1 ?p2 ?n2 }
        }
        LIMIT 40
    """

    "Query execution" should "retrieve result from a working data source" in {
        val forwarder = setupQueryExecutionForwarder(List(dbPediaDataSource))

        // Should first receive the progress message.
        (forwarder !? GetNextMessage) match {
            case m: QueryExecutionProgress => {
                assert(m.successResults.length == 1)
                assert(m.errorResults.length == 0)
                assert(m.unfinishedDataSources.length == 0)
            }
            case _ => assert(false)
        }

        // Then the result message.
        (forwarder !? GetNextMessage) match {
            case m: QueryExecutionResult => assert(m.data.length == 1)
            case _ => assert(false)
        }

        forwarder ! TerminateForwarder
    }

    it should "handle error from an invalid data source" in {
        val forwarder = setupQueryExecutionForwarder(List(invalidDataSource))

        // Should first receive the progress message.
        (forwarder !? GetNextMessage) match {
            case m: QueryExecutionProgress => {
                assert(m.successResults.length == 0)
                assert(m.errorResults.length == 1)
                assert(m.unfinishedDataSources.length == 0)
            }
            case _ => assert(false)
        }

        // Then the result message.
        (forwarder !? GetNextMessage) match {
            case m: QueryExecutionResult => assert(m.data.length == 0)
            case _ => assert(false)
        }

        forwarder ! TerminateForwarder
    }

    it should "support multiple data sources" in {
        val forwarder = setupQueryExecutionForwarder(List(dbPediaDataSource, dataGovDataSource, invalidDataSource))

        // Should receive three consecutive progress messages.
        (forwarder !? GetNextMessage) match {
            case m: QueryExecutionProgress => assert(m.unfinishedDataSources.length == 2)
            case _ => assert(false)
        }
        (forwarder !? GetNextMessage) match {
            case m: QueryExecutionProgress => assert(m.unfinishedDataSources.length == 1)
            case _ => assert(false)
        }
        (forwarder !? GetNextMessage) match {
            case m: QueryExecutionProgress => {
                assert(m.successResults.length == 2)
                assert(m.errorResults.length == 1)
                assert(m.unfinishedDataSources.length == 0)
            }
            case _ => assert(false)
        }

        // Then the result message.
        (forwarder !? GetNextMessage) match {
            case m: QueryExecutionResult => {
                assert(m.data.length == 2)
            }
            case _ => assert(false)
        }

        forwarder ! TerminateForwarder
    }

    "Analysis execution" should "work with initial setup" in {
        // Create the analysis
        val initialPlugin = new SparqlQuery()
        val initialPluginInstance = initialPlugin.createInstance()
        val analysis = new Analysis("Test analysis", None, initialPluginInstance)

        // Set up the analysis.
        initialPluginInstance.setParameter(initialPlugin.queryParameter, constructQuery)

        // Evaluate it.
        val analysisEvaluation = new AnalysisEvaluation(analysis, List(dbPediaDataSource), None)
        analysisEvaluation.start()

        // Wait for the result and then check whether it's valid.
        while (!analysisEvaluation.isFinished) {
            Thread.sleep(10)
        }
        assert(analysisEvaluation.result.map(_.isInstanceOf[AnalysisEvaluationSuccess]).getOrElse(false))
        analysisEvaluation ! TerminateEvaluation
    }

    it should "evaluate multiple plugins" in {
        val analysisEvaluation = createMultiplePluginAnalysis().evaluate(List(dbPediaDataSource))

        // Wait for the result.
        while (!analysisEvaluation.isFinished) {
            Thread.sleep(100)
        }

        // Check whether it's valid.
        analysisEvaluation.result.foreach {
            case success: AnalysisEvaluationSuccess => {
                val g = success.outputGraph
                assert(g.edges.isEmpty)
                assert(g.vertices.length == 1)
                g.vertices.head match {
                    case literal: LiteralNode => {
                        assert(literal.value.isInstanceOf[Int], "Invalid doubled count")
                    }
                    case _ => assert(false, "The only node is not a literal.")
                }
            }
            case _ => assert(false, "The analysis didn't succeeed.")
        }
        analysisEvaluation ! TerminateEvaluation
    }

    it should "support timeout" in {
        val analysisEvaluation = createMultiplePluginAnalysis().evaluate(List(dbPediaDataSource), Some(10))

        // Wait for the result.
        while (!analysisEvaluation.isFinished) {
            Thread.sleep(100)
        }

        // Check whether it's a timeout result.
        analysisEvaluation.result.foreach {
            case timeout: AnalysisEvaluationQueryTimeout => // OK.
            case _ => assert(false, "The analysis didn't end up in timeout result.")
        }
        analysisEvaluation ! TerminateEvaluation
    }

    private def createMultiplePluginAnalysis(): Analysis = {
        val initialPlugin = new SparqlQuery()
        val initialPluginInstance = initialPlugin.createInstance()
        val nodeCounter = new NodeCounter()
        val intDoubler = new IntDoubler()

        val analysis = new Analysis("Test analysis", None, initialPluginInstance)
        analysis.addPluginInstance(nodeCounter.createInstance())
        analysis.addPluginInstance(intDoubler.createInstance())
        initialPluginInstance.setParameter(initialPlugin.queryParameter, constructQuery)

        analysis
    }

    private def setupQueryExecutionForwarder(dataSources: Seq[DataSource]): Forwarder = {
        val forwarder = new Forwarder()
        val queryExecution = new QueryExecution(forwarder, dataSources, selectQuery)
        forwarder.start()
        queryExecution.start()
        forwarder
    }
}

object GetNextMessage

object TerminateForwarder

class Forwarder extends Actor
{
    val messageQueue = new Queue[Any]()

    def act() {
        loop {
            react {
                case GetNextMessage => {
                    val asker = sender
                    if (messageQueue.isEmpty) {
                        react {
                            case m => asker ! m
                        }
                    } else {
                        asker ! messageQueue.dequeue()
                    }
                }
                case TerminateForwarder => exit()
                case m => messageQueue.enqueue(m)
            }
        }
    }
}

class NodeCounter extends Plugin("Node counter", Nil)
{
    def evaluate(inputGraph: Graph, parameterInstances: Seq[ParameterValue[_]], progressReporter: Double => Unit) = {
        (1 to 10).foreach {i =>
            Thread.sleep(100)
            progressReporter(i / 10.0)
        }
        new Graph(List(new LiteralNode(inputGraph.vertices.length)), Nil)
    }
}

class IntDoubler extends Plugin("Int doubler", Nil)
{
    def evaluate(inputGraph: Graph, parameterInstances: Seq[ParameterValue[_]], progressReporter: Double => Unit) = {
        val doubled = inputGraph.vertices.headOption.flatMap {
            case l: LiteralNode => {
                l.value match {
                    case i: Int => Some(i + i)
                    case _ => None
                }
            }
            case _ => None
        }
        new Graph(List(new LiteralNode(doubled.getOrElse(0))), Nil)
    }
}
