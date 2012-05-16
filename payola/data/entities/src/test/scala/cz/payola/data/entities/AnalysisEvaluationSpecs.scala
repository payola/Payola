package cz.payola.data.entities

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.domain.entities.Analysis
import cz.payola.domain.entities.analyses._
import evaluation.Success
import plugins.data.SparqlEndpoint
import plugins.query._
import plugins.{Join, Union}
import cz.payola.data.entities.analyses.PluginDbRepresentation
import cz.payola.data.entities.dao.AnalysisDAO

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

        val analysisDao = new AnalysisDAO

        val plugins = List(
            sparqlEndpointPlugin,
            concreteSparqlQueryPlugin,
            projectionPlugin,
            selectionPlugin,
            typedPlugin,
            unionPlugin
        )

        // persist analysis
        val analysis = new Analysis("Cities with more than 2 million habitants with countries", None)
        analysisDao.persist(analysis)
        assert(analysisDao.getById(analysis.id).isDefined)

        // Persist  plugins
        for (p <- plugins) {
            plugDao.persist(p)
            assert(plugDao.getByName(p.name) != None)
        }

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

        analysisDao.persist(analysis)

        // Get analysis from DB
        val persistedAnalysis = analysisDao.getByID(analysis.id)
        assert(persistedAnalysis.isDefined)

        // .. and test it
        val evaluation = persistedAnalysis.get.evaluate()
        while (!evaluation.isFinished) {
            println("Not finished, current progress: " + evaluation.progress.value)
            Thread.sleep(1000)
        }
        val result = evaluation.result

        println("Done with result: " + result.toString)
        assert(result.map(_.isInstanceOf[Success]).getOrElse(false))
    }
}










