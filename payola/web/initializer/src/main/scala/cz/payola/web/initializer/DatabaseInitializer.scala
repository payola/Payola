package cz.payola.web.initializer

import cz.payola.domain.entities.analyses.plugins.data.SparqlEndpoint
import cz.payola.domain.entities.analyses.plugins.query._
import cz.payola.domain.entities.analyses.plugins._
import cz.payola.data.dao._
import cz.payola.data.entities._
import cz.payola.data.PayolaDB

object DatabaseInitializer extends App
{
    // Connect to DB and (re)created DB schema
    PayolaDB.connect()
    PayolaDB.createSchema()

    // Create initial data
    createInitialData()

    def createInitialData() {
        val sparqlEndpointPlugin = new SparqlEndpoint
        val concreteSparqlQueryPlugin = new ConcreteSparqlQuery
        val projectionPlugin = new Projection
        val selectionPlugin = new Selection
        val typedPlugin = new Typed
        val join = new Join
        val unionPlugin = new Union

        val analysisDao = new AnalysisDAO
        val pluginDao = new PluginDAO

        val plugins = List(
            sparqlEndpointPlugin,
            concreteSparqlQueryPlugin,
            projectionPlugin,
            selectionPlugin,
            typedPlugin,
            join,
            unionPlugin
        )
        // persist analysis
        //TODO: SQUERYL and "xx" id
        val analysis = new Analysis("xx", "DB: Cities with more than 2 million habitants with countries", None)
        analysisDao.persist(analysis)

        // Persist  plugins
        for (p <- plugins) {
            pluginDao.persist(p)
        }

        val citiesFetcher = sparqlEndpointPlugin.createInstance()
            .setParameter("EndpointURL", "http://dbpedia.org/sparql")
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

        val countriesFetcher = sparqlEndpointPlugin.createInstance()
            .setParameter("EndpointURL", "http://dbpedia.org/sparql")
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

        println("Data initialized")
    }
}
