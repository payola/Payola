package cz.payola.domain.entities.plugins.concrete

import collection.immutable
import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.plugins._
import cz.payola.domain.rdf.Graph
import cz.payola.domain.sparql._
import cz.payola.domain.entities.plugins.concrete.query.Construct

/**
  * A plugin that is used to fetch RDF data using SPARQL queries, fetch neighbourhood of a particular node etc.
  */
abstract class DataFetcher(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends Plugin(name, inputCount, parameters, id)
{
    private val selectEverythingQuery = """
        CONSTRUCT {
            ?s ?p ?v .
        }
        WHERE {
            ?s ?p ?v .
        }"""

    private val selectFirstTripleQuery = selectEverythingQuery + " LIMIT 1"

    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Option[Graph]], progressReporter: Double => Unit) = {
        evaluateWithQuery(instance, selectEverythingQuery, progressReporter)
    }

    /**
      * Evaluates the plugin.
      * @param instance The corresponding instance.
      * @param query The query used to filter data from the data source.
      * @param progressReporter A method that can be used to report plugin evaluation progress (which has to be within
      *                         the [0.0, 1.0] interval).
      * @return The output graph.
      */
    def evaluateWithQuery(instance: PluginInstance, query: String, progressReporter: Double => Unit): Graph = {
        executeQuery(instance, query)
    }

    /**
      * Executes the specified query.
      * @param instance The corresponding instance.
      * @param query The query to execute.
      * @return The result of the query.
      */
    def executeQuery(instance: PluginInstance, query: String): Graph

    /**
      * Returns the first available triple.
      * @param instance The corresponding instance.
      * @return The triple represented as a graph.
      */
    def getFirstTriple(instance: PluginInstance): Graph = {
        executeQuery(instance, selectFirstTripleQuery)
    }

    /**
      * Returns neighbourhood of the specified vertex.
      * @param instance The corresponding instance.
      * @param vertexURI URI of the vertex whose neighbourhood should be returned.
      * @param distance Maximal distance to travel from the vertex to its neighbours. To select only direct neighbours,
      *                 use 1, to select direct neighbours and their neighbours, use 2 etc. Note that particular data
      *                 fetchers may use some optimizations/heuristics so it's not guaranteed that this parameter will
      *                 be always taken into account.
      * @return The neighbourhood graph.
      */
    def getNeighbourhood(instance: PluginInstance, vertexURI: String, distance: Int = 1): Graph = {
        require(distance > 0, "The distance has to be a positive number.")
        val uri = vertexURI.trim

        if (uri.nonEmpty) {
            val subjectVariable = Variable("s1")
            val objectVariable = Variable("o1")
            val patterns = List(
                GraphPattern(
                    List(TriplePattern(subjectVariable, Variable("sp0"), Uri(vertexURI))),
                    GraphPattern.optionalProperties(subjectVariable)
                ),
                GraphPattern(
                    List(TriplePattern(Uri(vertexURI), Variable("op0"), objectVariable)),
                    GraphPattern.optionalProperties(objectVariable)
                )
            )

            patterns.par.map(p => executeQuery(instance, ConstructQuery(p).toString)).reduce(_ + _)
        } else {
            Graph.empty
        }

        /* TODO take the distance into account
        val neighbourTriplePatterns = (1 to (distance - 1)).flatMap { i =>
            List(
                TriplePattern(Variable("o" + i), Variable("op" + i), Variable("o" + (i + 1))),
                TriplePattern(Variable("s" + i), Variable("sp" + i), Variable("s" + (i + 1)))
            )
        }
        val triplePatterns = rootTriplePatterns ++ neighbourTriplePatterns
        val graphPattern = triplePatterns.foldRight(GraphPattern.empty)((t, g) => GraphPattern(List(t), List(g)))

        executeQuery(instance, ConstructQuery(triplePatterns, Some(graphPattern)).toString)*/

    }
}
