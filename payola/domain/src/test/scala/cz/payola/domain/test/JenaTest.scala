package cz.payola.domain.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.domain.entities.analyses.plugins.data.SparqlEndpoint
import cz.payola.domain.entities.analyses.plugins.query._
import cz.payola.domain.entities.analyses.plugins._
import cz.payola.domain.entities.Analysis
import cz.payola.domain.entities.analyses.evaluation.Success

/** This test shows that multiple calls of Jena's interface doesn't cause an application
  * crash even though it does when testing via SBT for the second time.
  */

/*
class JenaTest extends FlatSpec with ShouldMatchers
{

    def performTest = {
        val sparqlEndpointPlugin = new SparqlEndpoint
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


    "Jena" should "withstand several consecutive calls" in {
        for (i <- 0 until 25) {
            performTest
        }
    }

    "Jena" should "withstand several consecutive calls with time outs" in {
        for (i <- 0 until 25) {
            performTest
            Thread.sleep(2000) // Sleep for two seconds
        }
    }

}
*/
