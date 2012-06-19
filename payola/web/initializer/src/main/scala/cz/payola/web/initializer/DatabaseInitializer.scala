package cz.payola.web.initializer

import cz.payola.domain.entities.analyses.plugins.data.SparqlEndpoint
import cz.payola.domain.entities.analyses.plugins.query._
import cz.payola.domain.entities.analyses.plugins._
import cz.payola.data.dao._
import cz.payola.data.PayolaDB
import cz.payola.domain.entities.analyses.DataSource
import scala.collection.immutable

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
        val a = new cz.payola.domain.entities.Analysis("DB: Cities with more than 2 million habitants with countries", None)
        val analysis = analysisDao.persist(a).get

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

        val ds1 = new DataSource("Cities", None, sparqlEndpointPlugin, immutable.Seq(sparqlEndpointPlugin.parameters(0).asInstanceOf[cz.payola.domain.entities.analyses.parameters.StringParameter].createValue("http://dbpedia.org/ontology/Country")))
        val ds2 = new DataSource("Countries", None, sparqlEndpointPlugin, immutable.Seq(sparqlEndpointPlugin.parameters(0).asInstanceOf[cz.payola.domain.entities.analyses.parameters.StringParameter].createValue("http://dbpedia.org/ontology/City")))
        val ds3 = new DataSource("Countries2", None, sparqlEndpointPlugin, immutable.Seq(sparqlEndpointPlugin.parameters(0).asInstanceOf[cz.payola.domain.entities.analyses.parameters.StringParameter].createValue("http://dbpedia.org/ontology/City")))

        println("Data initialized")
    }
}
