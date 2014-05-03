package cz.payola.web.initializer

import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.domain.entities.plugins.concrete._
import cz.payola.domain.entities.plugins.concrete.data._
import cz.payola.domain.entities.plugins.concrete.query._
import cz.payola.domain.entities.settings.OntologyCustomization
import cz.payola.data.squeryl.SquerylDataContextComponent
import cz.payola.web.shared.Payola
import scala.Some

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
        val virtuosoSecuredPlugin = new VirtuosoSecuredEndpointFetcher
        val preparedQuery = new PreparedSparqlQuery

        /** JH */
        val limit = new Limit

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
            shortestPathPlugin,
            virtuosoSecuredPlugin,
            limit,
            preparedQuery
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
        if (!pluginsDirectory.exists()) {
            pluginsDirectory.mkdirs()
        }

        // Create the admin.
        val admin = Payola.model.userModel.create(Payola.settings.adminEmail, "payola!")

        List(
            new Prefix("pc", "pc", "http://purl.org/procurement/public-contracts#", None),
            new Prefix("oc", "oc", "http://opencoinage.org/rdf/", None),
            new Prefix("dv", "dv", "http://rdf.data-vocabulary.org/#", None),
            new Prefix("dawgt", "dawgt", "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#", None),
            new Prefix("dbpprop", "dbpprop", "http://dbpedia.org/property/", None),
            new Prefix("dbpedia", "dbpedia", "http://dbpedia.org/resource/", None),
            new Prefix("dc", "dc", "http://purl.org/dc/elements/1.1/", None),
            new Prefix("fn", "fn", "http://www.w3.org/2005/xpath-functions/#", None),
            new Prefix("foaf", "foaf", "http://xmlns.com/foaf/0.1/", None),
            new Prefix("geo", "geo", "http://www.w3.org/2003/01/geo/wgs84_pos#", None),
            new Prefix("go", "go", "http://purl.org/obo/owl/GO#", None),
            new Prefix("math", "math", "http://www.w3.org/2000/10/swap/math#", None),
            new Prefix("mesh", "mesh", "http://purl.org/commons/record/mesh/", None),
            new Prefix("mf", "mf", "http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#", None),
            new Prefix("nci", "nci", "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#", None),
            new Prefix("obo", "obo", "http://www.geneontology.org/formats/oboInOwl#", None),
            new Prefix("owl", "owl", "http://www.w3.org/2002/07/owl#", None),
            new Prefix("product", "product", "http://www.buy.com/rss/module/productV2/", None),
            new Prefix("protseq", "protseq", "http://purl.org/science/protein/bysequence/", None),
            new Prefix("rdf", "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#", None),
            new Prefix("rdfa", "rdfa", "http://www.w3.org/ns/rdfa#", None),
            new Prefix("rdfdf", "rdfdf", "http://www.openlinksw.com/virtrdf-data-formats#", None)
        ).foreach(model.prefixRepository.persist(_))

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
                	?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
                	    <http://purl.org/procurement/public-contracts#Contract> .
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

        // Persist the ontology customizations with some default colors and strokes.
        val urls = List("http://opendata.cz/pco/public-contracts.xml")
        val customization = OntologyCustomization.empty(urls, "Public contracts", Some(admin))
        customization.isPublic = true;

        getPublicContractsOntologySettings.foreach { classSettings =>
            val classCustomization = customization.classCustomizations.find(_.uri == classSettings._1).get
            classCustomization.fillColor = classSettings._2._1._1
            classCustomization.radius = classSettings._2._1._2
            classCustomization.glyph = classSettings._2._1._3

            classSettings._2._2.foreach { propertySettings =>
                val propCustomization = classCustomization.propertyCustomizations.find(_.uri == propertySettings._1).get
                propCustomization.strokeColor = propertySettings._2._1
                propCustomization.strokeWidth = propertySettings._2._2
            }
        }


        val populatedCities = new cz.payola.domain.entities.Analysis("Populated cities", Some(admin))
        populatedCities.isPublic = true
        val populatedCitiesPersisted = model.analysisRepository.persist(populatedCities)

        val populatedcitiesFetcher = sparqlEndpointPlugin.createInstance().setParameter(
            SparqlEndpointFetcher.endpointURLParameter, "http://dbpedia.org/sparql")
        val populatedcitiesTyped = typedPlugin.createInstance().setParameter(
            Typed.typeURIParameter, "http://dbpedia.org/ontology/City")
        val populatedcitiesPropertySelection = propertySelectionPlugin.createInstance().setParameter(
            PropertySelection.propertyURIsParameter,
            "http://dbpedia.org/ontology/populationAsOf\nhttp://dbpedia.org/ontology/populationTotal")

        populatedCitiesPersisted.addPluginInstances(populatedcitiesFetcher, populatedcitiesTyped, populatedcitiesPropertySelection)
        populatedCitiesPersisted.addBinding(populatedcitiesFetcher,populatedcitiesTyped)
        populatedCitiesPersisted.addBinding(populatedcitiesTyped,populatedcitiesPropertySelection)



        model.customizationRepository.persist(customization)
    }

    /*
      This returns list contains settings for the Public contracts ontology, the form is:
      List of (classUrl, ClassCustomizationSettings) tuples, where
          ClassCustomizationSettings is tuple of ( (color, radius, glyph), List of PropertyCustomizationSettings ),
              where PropertyCustomizationSetting is tuple of ( propertyUrl, tuple of (strokeColor, strokeWidth) ).
    */
    private def getPublicContractsOntologySettings = List(
        (
            "http://purl.org/procurement/public-contracts#Tender",
            (
                ("rgba(154,50,205,1.0)", 25, "%"),
                List(
                    ("http://purl.org/procurement/public-contracts#offeredPrice", ("rgba(176,23,31,1.0)", 3)),
                    ("http://purl.org/procurement/public-contracts#supplier", ("rgba(255,174,185,1.0)", 2))
                )
            )
        ),
        (
            "http://purl.org/procurement/public-contracts#Contract",
            (
                ("rgba(34,139,34,1.0)", 30, "'"),
                List(
                    ("http://purl.org/procurement/public-contracts#numberOfTenders", ("rgba(0,191,255,1.0)", 2)),
                    ("http://purl.org/procurement/public-contracts#contractingAuthority", ("rgba(238,238,0,1.0)", 3)),
                    ("http://purl.org/procurement/public-contracts#contractPrice", ("rgba(176,23,31,1.0)", 2))
                )
            )
        )
    )
}

