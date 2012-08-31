package cz.payola.domain.test

import org.scalatest.FlatSpec

import org.scalatest.matchers.ShouldMatchers
import cz.payola.domain.entities.plugins.concrete.data.SparqlEndpointFetcher
import cz.payola.domain.entities.plugins.concrete.query._
import cz.payola.domain.entities.analyses.evaluation.Success
import cz.payola.domain.entities.Analysis

class AnalysisEvaluationSpec extends FlatSpec with ShouldMatchers
{
    "Analysis evaluation" should "work" in {
        val sparqlEndpointPlugin = new SparqlEndpointFetcher
        val propertySelectionPlugin = new PropertySelection
        val filterPlugin = new Filter
        val typedPlugin = new Typed

        val analysis = new Analysis("Cities with more than 2 million habitants", None)
        val citiesFetcher = sparqlEndpointPlugin.createInstance()
            .setParameter(SparqlEndpointFetcher.endpointURLParameter, "http://dbpedia.org/sparql")
        val citiesTyped = typedPlugin.createInstance().setParameter(Typed.typeURIParameter,
            "http://dbpedia.org/ontology/City")
        val citiesPropertySelection = propertySelectionPlugin.createInstance().setParameter(
            PropertySelection.propertyURIsParameter,
            List("http://dbpedia.org/ontology/populationDensity", "http://dbpedia.org/ontology/populationTotal"
        ).mkString("\n"))
        val citiesFilter = filterPlugin.createInstance().setParameter(
            Filter.propertyURIParameter, "http://dbpedia.org/ontology/populationTotal"
        ).setParameter(
            Filter.operatorParameter, ">"
        ).setParameter(
            Filter.valueParameter, "2000000"
        )
        analysis.addPluginInstances(citiesFetcher, citiesTyped, citiesPropertySelection, citiesFilter)
        analysis.addBinding(citiesFetcher, citiesTyped)
        analysis.addBinding(citiesTyped, citiesPropertySelection)
        analysis.addBinding(citiesPropertySelection, citiesFilter)

        val evaluation = analysis.evaluate()
        while (!evaluation.isFinished) {
            Thread.sleep(1000)
        }
        val result = evaluation.getResult
        assert(result.map(_.isInstanceOf[Success]).getOrElse(false),
            "The analysis result isn't success. It's " + result)
    }
}
