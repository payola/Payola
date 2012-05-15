package cz.payola.data.entities.dao

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.entities.{PayolaDB, Analysis, User}
import cz.payola.domain.entities.analyses.plugins.data.SparqlEndpoint
import cz.payola.domain.entities.analyses.plugins.query.{Selection, Projection, Typed}
import cz.payola.domain.entities.analyses.plugins.Join

class AnalysisDAO extends EntityDAO[Analysis](PayolaDB.analyses)
{
    private val EVERY_USER: String = "00000000-0000-0000-0000-000000000000";

    override def getById(id: String) = {
        Some(analysis)
    }

    def getTopAnalyses(count: Int = 10): collection.Seq[Analysis] = {
        getTopAnalysesByUser(EVERY_USER)
    }

    def getTopAnalysesByUser(userId: String, count: Int = 10): collection.Seq[Analysis] = {
        require(count >= 0, "Count must be >= 0")
        // Get by all users or just by specified one
        //val query = table.where(a => userId === EVERY_USER or a.ownerId.getOrElse("").toString === userId)

        //evaluateCollectionResultQuery(query, 0, count)
        List(analysis)
    }

    def getPublicAnalysesByOwner(o: User, page: Int = 1, pageLength: Int = 0) = {
        /*val query = table.where(a => a.ownerId.getOrElse("") === o.id)

        transaction {
            query.page(page, pageLength).toSeq
        } */
        List(analysis)
    }


    val sparqlEndpointPlugin = new SparqlEndpoint
    val analysis = new Analysis("Cities with more than 2 million habitants with countries", None)
    val projectionPlugin = new Projection
    val selectionPlugin = new Selection
    val typedPlugin = new Typed
    val join = new Join

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
}
