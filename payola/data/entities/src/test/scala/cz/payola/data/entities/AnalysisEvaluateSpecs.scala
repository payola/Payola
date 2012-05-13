package cz.payola.data.entities

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import cz.payola.domain.entities.analyses.plugins.data.SparqlEndpoint
import cz.payola.domain.entities.analyses.plugins.query._

class AnalysisEvaluateSpecs extends FlatSpec with ShouldMatchers
{
    /*
    "Analysis evaluation" should "work" in {
        // TODO persistance:    Following plugins should be persisted after the database is created. They are the
        // TODO persistance:    default plugins that should be in the db from the beginning. When this is done, the
        // TODO persistance:    varaibles sparqlEndpointPlugin, concreteSparqlQueryPlugin... should be populated
        // TODO persistance:    not using the explicit plugin constructors, but retrieved from the db like this:
        // TODO persistance:        val sparqlEndpointPlugin = pluginDAO.getPluginByName("SPARQL Endpoint")
        val sparqlEndpointPlugin = new SparqlEndpoint
        val concreteSparqlQueryPlugin = new ConcreteSparqlQuery
        val projectionPlugin = new Projection
        val selectionPlugin = new Selection
        val typedPlugin = new Typed
        val leftJoinPlugin = new LeftJoin
        val unionPlugin = new Union

        val analysis = new Analysis("Cities with more than 2 million habitants", None)
        // TODO persistance:    The analysis should be persisted here.

        val i1 = sparqlEndpointPlugin.createInstance().setParameter("EndpointURL", "http://dbpedia.org/sparql")
        val i2 = typedPlugin.createInstance().setParameter("TypeURI", "http://dbpedia.org/ontology/City")
        val i3 = projectionPlugin.createInstance().setParameter("PropertyURIs", List(
            "http://dbpedia.org/ontology/populationDensity", "http://dbpedia.org/ontology/populationMetro",
            "http://dbpedia.org/ontology/populationUrban", "http://dbpedia.org/ontology/populationTotal"
        ).mkString("\n"))
        val i4 = selectionPlugin.createInstance().setParameter(
            "PropertyURI", "http://dbpedia.org/ontology/populationUrban"
        ).setParameter(
            "Operator", ">"
        ).setParameter(
            "Value", "2000000"
        )
        // TODO persistance:    The instances should be persisted here. I think that the plugin instance should also
        // TODO persistance:    take care of parameter value persistance, so if you persist an instance, the parameter
        // TODO persistance:    values would automatically become persisted too. Or another approach is to persist the
        // TODO persistance:    instance in analysis.addPluginInstance method - check whether the passed instance is
        // TODO persistance:    already persisted in the db and if not, then persist it first.

        // TODO persistance:    The following method calls should reflect its effects to the db immediately (the same
        // TODO persistance:    applies for removePluginInstance, removeBinding).
        analysis.addPluginInstances(i1, i2, i3, i4)
        analysis.addBinding(i1, i2)
        analysis.addBinding(i2, i3)
        analysis.addBinding(i3, i4)

        // TODO persistance:    In this point, you should be able to retrieve the analysis by id.
        // TODO persistance:        val evaluation = analysisDAO.getById("1234566").evaluate()
        val evaluation = analysis.evaluate()
        while (!evaluation.isFinished) {
            println("Not finished, current progress: " + evaluation.progress.value)
            Thread.sleep(1000)
        }
        val result = evaluation.result
        println("Done with result: " + result.toString)
        assert(result.map(_.isInstanceOf[Success]).getOrElse(false))
    }
    */
}
