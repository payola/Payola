package cz.payola.web.initializer

import cz.payola.domain.entities.plugins.DataSource
import cz.payola.domain.entities.plugins.concrete._
import cz.payola.domain.entities.plugins.concrete.data._
import cz.payola.domain.entities.plugins.concrete.query._
import cz.payola.data.squeryl.SquerylDataContextComponent
import cz.payola.web.shared.Payola
import cz.payola.domain.entities.settings.OntologyCustomization
import cz.payola.common.entities.User

/**
 * Runing this object will drop existing database, create new database and fill it with initial data.
 */
object DatabaseInitializer extends App
{

    val columnChartSPARQLQuery = """CONSTRUCT {
                                   |	?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/procurement/public-contracts#Contract> .
                                   |	?s <http://purl.org/goodrelations/v1#hasCurrencyValue> ?d .
                                   |	?s <http://purl.org/dc/terms/title> ?e .
                                   |} WHERE {
                                   |	?s <http://purl.org/procurement/public-contracts#agreedPrice> ?o .
                                   |	?o <http://purl.org/goodrelations/v1#hasCurrencyValue> ?d . FILTER (?d > 20000000000)
                                   |	?s <http://purl.org/dc/terms/title> ?e .
                                   |}""".stripMargin

    private val model = Payola.model.asInstanceOf[SquerylDataContextComponent]

    model.schema.wrapInTransaction {
        print("Recreating the database schema ... ")
        model.schema.recreate()
        println("OK")

        print("Persisting the initial data ... ")
        persistInitialData()
        println("OK")
    }

    private def persistInitialData() {
        val sparqlEndpointPlugin = new SparqlEndpointFetcher
        val payolaStoragePlugin = new PayolaStorage
        val openDataCleanStoragePlugin = new OpenDataCleanStorage
        val concreteSparqlQueryPlugin = new ConcreteSparqlQuery
        val projectionPlugin = new Projection
        val selectionPlugin = new Selection
        val typedPlugin = new Typed
        val join = new Join
        val unionPlugin = new Union
        val ontologicalFilterPlugin = new OntologicalFilter
        val shortestPathPlugin = new ShortestPath

        // Persist plugins.
        List(
            sparqlEndpointPlugin,
            payolaStoragePlugin,
            concreteSparqlQueryPlugin,
            concreteSparqlQueryPlugin,
            projectionPlugin,
            selectionPlugin,
            typedPlugin,
            join,
            unionPlugin,
            ontologicalFilterPlugin,
            shortestPathPlugin,
            openDataCleanStoragePlugin
        ).foreach { p =>
            model.pluginRepository.persist(p)
        }

        // Create the admin.
        val admin = Payola.model.userModel.create("admin@payola.cz", "payola!")

        // Persist data sources.
        List(
            DataSource("DBpedia.org", Some(admin),
                sparqlEndpointPlugin.createInstance().setParameter("EndpointURL", "http://dbpedia.org/sparql")),
            DataSource("Opendata.cz", Some(admin),
                sparqlEndpointPlugin.createInstance().setParameter("EndpointURL", "http://ld.opendata.cz:8894/sparql"))
        ).foreach { d =>
            d.isPublic = true
            model.dataSourceRepository.persist(d)
        }

        // persist analysis
        val a = new cz.payola.domain.entities.Analysis(
            "DB: Cities with more than 2 million habitants with countries",
            Some(admin))
        a.isPublic = true
        val analysis = model.analysisRepository.persist(a)

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

        val url = "http://opendata.cz/pco/public-contracts.xml"
        val customization = OntologyCustomization.empty(url, "Public contracts", Some(admin))
        model.ontologyCustomizationRepository.persist(customization)

        // Column-chart analyses
        val cca1 = new cz.payola.domain.entities.Analysis(
            "Really expensive public contracts",
            Some(admin))
        a.isPublic = true
        val columnChartAnalysis1 = model.analysisRepository.persist(cca1)
        val columnFetcher1 = sparqlEndpointPlugin.createInstance().
            setParameter("EndpointURL", "http://ld.opendata.cz:8894/sparql")
        val sparqlQuery = concreteSparqlQueryPlugin.createInstance().setParameter("Query", columnChartSPARQLQuery)

        columnChartAnalysis1.addPluginInstances(columnFetcher1, sparqlQuery)
        columnChartAnalysis1.addBinding(columnFetcher1, sparqlQuery)



        val cca2 = new cz.payola.domain.entities.Analysis(
            "Many tenders",
            Some(admin))
        a.isPublic = true
        val columnChartAnalysis2 = model.analysisRepository.persist(cca2)
        val columnFetcher2 = sparqlEndpointPlugin.createInstance().
            setParameter("EndpointURL", "http://ld.opendata.cz:8894/sparql")
        val contractTyped = typedPlugin.createInstance().setParameter("TypeURI", "http://purl.org/procurement/public-contracts#Contract")
        val contractSelection = selectionPlugin.createInstance().setParameter(
            "PropertyURI", "http://purl.org/procurement/public-contracts#numberOfTenders"
        ).setParameter(
            "Operator", ">"
        ).setParameter("Value", "120")

        columnChartAnalysis2.addPluginInstances(columnFetcher2, contractTyped, contractSelection)
        columnChartAnalysis2.addBinding(columnFetcher2, contractTyped)
        columnChartAnalysis2.addBinding(contractTyped, contractSelection)

    }

}

