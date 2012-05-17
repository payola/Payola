package cz.payola.domain.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import scala.actors.Actor
import scala.collection.mutable.Queue
import cz.payola.domain.entities.Analysis
import cz.payola.domain.entities.analyses._
import cz.payola.domain.rdf._
import evaluation.Success
import plugins.data.SparqlEndpoint
import plugins.query._
import plugins.{Join, Union}

class AnalysisEvaluationSpecs extends FlatSpec with ShouldMatchers
{
    "Analysis evaluation" should "work" in {
        val sparqlEndpointPlugin = new SparqlEndpoint
        val concreteSparqlQueryPlugin = new ConcreteSparqlQuery
        val projectionPlugin = new Projection
        val selectionPlugin = new Selection
        val typedPlugin = new Typed
        val join = new Join
        val unionPlugin = new Union

        val analysis = new Analysis("Cities with more than 2 million habitants with countries", None)

        val citiesFetcher = sparqlEndpointPlugin.createInstance().setParameter("EndpointURL", "http://dbpedia.org/sparql")
        val citiesTyped = typedPlugin.createInstance().setParameter("TypeURI", "http://dbpedia.org/ontology/City")
        val citiesProjection = projectionPlugin.createInstance().setParameter("PropertyURIs", List(
            "http://dbpedia.org/ontology/populationDensity", "http://dbpedia.org/ontology/populationTotal"
        ).mkString("\n"))
        val citiesSelection = selectionPlugin.createInstance().setParameter(
            "PropertyURI", "http://dbpedia.org/ontology/populationTotal"
        ).setParameter(
            "Operator", ">"
        ).setParameter(
            "Value", "2000000"
        )
        analysis.addPluginInstances(citiesFetcher, citiesTyped, citiesProjection, citiesSelection)
        analysis.addBinding(citiesFetcher, citiesTyped)
        analysis.addBinding(citiesTyped, citiesProjection)
        analysis.addBinding(citiesProjection, citiesSelection)

        val countriesFetcher = sparqlEndpointPlugin.createInstance().setParameter("EndpointURL", "http://dbpedia.org/sparql")
        val countriesTyped = typedPlugin.createInstance().setParameter("TypeURI", "http://dbpedia.org/ontology/Country")
        val countriesProjection = projectionPlugin.createInstance().setParameter("PropertyURIs", List(
            "http://dbpedia.org/ontology/areaTotal"
        ).mkString("\n"))
        analysis.addPluginInstances(countriesFetcher, countriesTyped, countriesProjection)
        analysis.addBinding(countriesFetcher, countriesTyped)
        analysis.addBinding(countriesTyped, countriesProjection)

        val citiesCountriesJoin = join.createInstance().setParameter(
            "JoinPropertyURI", "http://dbpedia.org/ontology/country"
        ).setParameter(
            "IsInner", false
        )
        analysis.addPluginInstances(citiesCountriesJoin)
        analysis.addBinding(citiesSelection, citiesCountriesJoin, 0)
        analysis.addBinding(countriesProjection, citiesCountriesJoin, 1)

        val evaluation = analysis.evaluate()
        while (!evaluation.isFinished) {
            println("Not finished, current progress: " + evaluation.getProgress.value)
            Thread.sleep(1000)
        }
        val result = evaluation.getResult
        println("Done with result: " + result.toString)
        assert(result.map(_.isInstanceOf[Success]).getOrElse(false))
    }

    val selectQuery = "select distinct ?Concept where {[] a ?Concept} LIMIT 100";

    /*val constructQuery = """
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
    }*/
}

/*
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

class NodeCounter extends Plugin("Node counter", 1, Nil)
{
    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Graph], progressReporter: Double => Unit): Graph = {
        (1 to 10).foreach { i =>
            Thread.sleep(100)
            progressReporter(i / 10.0)
        }
        new Graph(List(new LiteralNode(inputs(0).vertices.length)), Nil)
    }
}

class IntDoubler extends Plugin("Int doubler", 1, Nil)
{
    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Graph], progressReporter: Double => Unit): Graph = {
        val doubled = inputs(0).vertices.headOption.flatMap {
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
}*/
