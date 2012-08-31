package cz.payola.web.initializer

import cz.payola.domain.entities.Analysis
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.domain.entities.plugins.concrete._
import cz.payola.domain.entities.plugins.concrete.data._
import cz.payola.domain.entities.plugins.concrete.query._
import cz.payola.domain.entities.settings.OntologyCustomization
import cz.payola.data.squeryl.SquerylDataContextComponent
import cz.payola.web.shared.Payola

/**
 * Running this object will drop the existing database, create a new one and fill it with the initial data.
 */
object DatabaseInitializer extends App
{
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
        val cleanStoragePlugin = new OpenDataCleanStorage
        val concreteSparqlQueryPlugin = new ConcreteSparqlQuery
        val propertySelectionPlugin = new PropertySelection
        val filterPlugin = new Filter
        val typedPlugin = new Typed
        val join = new Join
        val unionPlugin = new Union
        val ontologicalFilterPlugin = new OntologicalFilter
        val shortestPathPlugin = new ShortestPath

        val publicPlugins = List(
            sparqlEndpointPlugin,
            cleanStoragePlugin,
            concreteSparqlQueryPlugin,
            concreteSparqlQueryPlugin,
            propertySelectionPlugin,
            filterPlugin,
            typedPlugin,
            join,
            unionPlugin,
            ontologicalFilterPlugin,
            shortestPathPlugin
        )

        val privatePlugins = List(
            payolaStoragePlugin
        )

        val plugins = publicPlugins ++ privatePlugins

        // Persist the plugins.
        publicPlugins.foreach(_.isPublic = true)
        privatePlugins.foreach(_.isPublic = false)
        plugins.foreach(model.pluginRepository.persist(_))

        // Create plugins directory
        val pluginsDirectory = Payola.settings.pluginDirectory
        if (!pluginsDirectory.exists()){
            pluginsDirectory.mkdirs()
        }

        // Create the admin.
        val admin = Payola.model.userModel.create(Payola.settings.adminEmail, "payola!")

        // Persist the data sources.
        List(
            DataSource("DBpedia.org", Some(admin), sparqlEndpointPlugin.createInstance().setParameter(
                SparqlEndpointFetcher.endpointURLParameter, "http://dbpedia.org/sparql")),
            DataSource("Opendata.cz", Some(admin), sparqlEndpointPlugin.createInstance().setParameter(
                SparqlEndpointFetcher.endpointURLParameter, "http://ld.opendata.cz:8894/sparql"))
        ).foreach { d =>
            d.isPublic = true
            model.dataSourceRepository.persist(d)
        }

        // Persist the analyses
        val bigCities = new cz.payola.domain.entities.Analysis("Big cities with countries", Some(admin))
        bigCities.isPublic = true
        val bigCitiesPersisted = model.analysisRepository.persist(bigCities)

        val citiesFetcher = sparqlEndpointPlugin.createInstance().setParameter(
            SparqlEndpointFetcher.endpointURLParameter, "http://dbpedia.org/sparql")
        val citiesTyped = typedPlugin.createInstance().setParameter(
            Typed.typeURIParameter, "http://dbpedia.org/ontology/City")
        val citiesPropertySelection = propertySelectionPlugin.createInstance().setParameter(
            PropertySelection.propertyURIsParameter,
            "http://dbpedia.org/ontology/populationDensity\nhttp://dbpedia.org/ontology/populationTotal")
        val citiesFilter = filterPlugin.createInstance().setParameter(
            Filter.propertyURIParameter, "http://dbpedia.org/ontology/populationTotal"
        ).setParameter(
            Filter.operatorParameter, ">"
        ).setParameter(
            Filter.valueParameter, "2000000"
        )
        bigCitiesPersisted.addPluginInstances(citiesFetcher, citiesTyped, citiesPropertySelection, citiesFilter)
        bigCitiesPersisted.addBinding(citiesFetcher, citiesTyped)
        bigCitiesPersisted.addBinding(citiesTyped, citiesPropertySelection)
        bigCitiesPersisted.addBinding(citiesPropertySelection, citiesFilter)

        val countriesFetcher = sparqlEndpointPlugin.createInstance().setParameter(
            SparqlEndpointFetcher.endpointURLParameter, "http://dbpedia.org/sparql")
        val countriesTyped = typedPlugin.createInstance().setParameter(
            Typed.typeURIParameter, "http://dbpedia.org/ontology/Country")
        val countriesPropertySelection = propertySelectionPlugin.createInstance().setParameter(
            PropertySelection.propertyURIsParameter, "http://dbpedia.org/ontology/areaTotal")
        bigCitiesPersisted.addPluginInstances(countriesFetcher, countriesTyped, countriesPropertySelection)
        bigCitiesPersisted.addBinding(countriesFetcher, countriesTyped)
        bigCitiesPersisted.addBinding(countriesTyped, countriesPropertySelection)

        val citiesCountriesJoin = join.createInstance().setParameter(
            Join.propertyURIParameter, "http://dbpedia.org/ontology/country"
        ).setParameter(
            Join.isInnerParameter, false
        )
        bigCitiesPersisted.addPluginInstances(citiesCountriesJoin)
        bigCitiesPersisted.addBinding(citiesFilter, citiesCountriesJoin, 0)
        bigCitiesPersisted.addBinding(countriesPropertySelection, citiesCountriesJoin, 1)


        val expensiveContracts = new Analysis("Really expensive public contracts", Some(admin))
        expensiveContracts.isPublic = true
        val expensiveContractsPersisted = model.analysisRepository.persist(expensiveContracts)

        val expensiveContractsFetcher = sparqlEndpointPlugin.createInstance().setParameter(
            SparqlEndpointFetcher.endpointURLParameter, "http://ld.opendata.cz:8894/sparql")
        val expensiveContractsQuery = concreteSparqlQueryPlugin.createInstance().setParameter(
            ConcreteSparqlQuery.queryParameter,
            """
                CONSTRUCT {
                	?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/procurement/public-contracts#Contract> .
                	?s <http://purl.org/goodrelations/v1#hasCurrencyValue> ?d .
                	?s <http://purl.org/dc/terms/title> ?e .
                } WHERE {
                	?s <http://purl.org/procurement/public-contracts#agreedPrice> ?o .
                	?o <http://purl.org/goodrelations/v1#hasCurrencyValue> ?d . FILTER (?d > 20000000000)
                	?s <http://purl.org/dc/terms/title> ?e .
                }
            """)
        expensiveContractsPersisted.addPluginInstances(expensiveContractsFetcher, expensiveContractsQuery)
        expensiveContractsPersisted.addBinding(expensiveContractsFetcher, expensiveContractsQuery)


        val manyTenders = new cz.payola.domain.entities.Analysis("Contracts with many tenders", Some(admin))
        manyTenders.isPublic = true
        val manyTendersPersisted = model.analysisRepository.persist(manyTenders)

        val contractFetcher = sparqlEndpointPlugin.createInstance().setParameter(
            SparqlEndpointFetcher.endpointURLParameter, "http://ld.opendata.cz:8894/sparql")
        val contractTyped = typedPlugin.createInstance().setParameter(
            Typed.typeURIParameter, "http://purl.org/procurement/public-contracts#Contract")
        val contractFilter = filterPlugin.createInstance().setParameter(
            Filter.propertyURIParameter, "http://purl.org/procurement/public-contracts#numberOfTenders"
        ).setParameter(
            Filter.operatorParameter, ">"
        ).setParameter(
            Filter.valueParameter, "120"
        )
        val contractTitlePropertySelection = propertySelectionPlugin.createInstance().setParameter(
            PropertySelection.selectPropertyInfoParameter, false
        ).setParameter(
            PropertySelection.propertyURIsParameter, "http://purl.org/dc/terms/title"
        )

        manyTendersPersisted.addPluginInstances(
            contractFetcher,
            contractTyped,
            contractFilter,
            contractTitlePropertySelection
        )
        manyTendersPersisted.addBinding(contractFetcher, contractTyped)
        manyTendersPersisted.addBinding(contractTyped, contractFilter)
        manyTendersPersisted.addBinding(contractFilter, contractTitlePropertySelection)


        // Persist the ontology customizations.
        val url = "http://opendata.cz/pco/public-contracts.xml"
        val customization = OntologyCustomization.empty(url, "Public contracts", Some(admin))
        model.ontologyCustomizationRepository.persist(customization)
    }
}

