package cz.payola.web.initializer

import cz.payola.domain.entities.plugins.concrete._
import cz.payola.domain.entities.plugins.concrete.data._
import cz.payola.domain.entities.plugins.concrete.query._
import cz.payola.web.shared.Payola
import cz.payola.data.squeryl.SquerylDataContextComponent

object DatabaseInitializer extends App
{
    print("Recreating the database schema ... ")
    Payola.model.asInstanceOf[SquerylDataContextComponent].schema.recreate()
    println("OK")

    print("Persisting the initial data ... ")

    val sparqlEndpointPlugin = new SparqlEndpoint
    val payolaStoragePlugin = new PayolaStorage
    val concreteSparqlQueryPlugin = new ConcreteSparqlQuery
    val projectionPlugin = new Projection
    val selectionPlugin = new Selection
    val typedPlugin = new Typed
    val join = new Join
    val unionPlugin = new Union
    val ontologicalFilterPlugin = new OntologicalFilter
    val shortestPathPlugin = new ShortestPath

    val plugins = List(
        sparqlEndpointPlugin,
        payolaStoragePlugin,
        concreteSparqlQueryPlugin,
        projectionPlugin,
        selectionPlugin,
        typedPlugin,
        join,
        unionPlugin,
        ontologicalFilterPlugin,
        shortestPathPlugin
    )

    // TODO persists the plugins and maybe some data sources like dbpedia, opendata.cz etc.

    println("OK")

    /*
    // persist analysis
    val a = new cz.payola.domain.entities.Analysis("DB: Cities with more than 2 million habitants with countries", None)
    a.isPublic_=(true)
    val analysis = analysisDao.persist(a)

    // Persist  plugins
    plugins.foreach(p => pluginDao.persist(p))

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

    val ds1 = new DataSource("Cities", None, sparqlEndpointPlugin, immutable.Seq(sparqlEndpointPlugin.parameters(0).asInstanceOf[cz.payola.domain.entities.plugins.parameters.StringParameter].createValue("http://dbpedia.org/ontology/Country")))
    val ds2 = new DataSource("Countries", None, sparqlEndpointPlugin, immutable.Seq(sparqlEndpointPlugin.parameters(0).asInstanceOf[cz.payola.domain.entities.plugins.parameters.StringParameter].createValue("http://dbpedia.org/ontology/City")))
    val ds3 = new DataSource("Countries2", None, sparqlEndpointPlugin, immutable.Seq(sparqlEndpointPlugin.parameters(0).asInstanceOf[cz.payola.domain.entities.plugins.parameters.StringParameter].createValue("http://dbpedia.org/ontology/City")))

    val dsDao = new DataSourceDAO()
    ds1.isPublic_=(true)
    ds2.isPublic_=(true)
    ds3.isPublic_=(true)

    dsDao.persist(ds1)
    dsDao.persist(ds2)
    dsDao.persist(ds3)

    println("Data initialized")*/
}

