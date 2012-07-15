package cz.payola.web.initializer

import cz.payola.domain.entities.plugins.DataSource
import cz.payola.domain.entities.plugins.concrete._
import cz.payola.domain.entities.plugins.concrete.data._
import cz.payola.domain.entities.plugins.concrete.query._
import cz.payola.data.squeryl.SquerylDataContextComponent
import cz.payola.web.shared.Payola

object DatabaseInitializer extends App
{
    private val model = Payola.model.asInstanceOf[SquerylDataContextComponent]

    print("Recreating the database schema ... ")
    model.schema.recreate()
    println("OK")

    print("Persisting the initial data ... ")
    persistInitialData()
    println("OK")

    private def persistInitialData() {
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

        // Persist plugins.
        List(
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
        ).foreach { p =>
            model.pluginRepository.persist(p)
        }

        // Persist data sources.
        List(
            DataSource("DBpedia.org", None,
                sparqlEndpointPlugin.createInstance().setParameter("EndpointURL", "http://dbpedia.org/sparql")),
            DataSource("Opendata.cz", None,
                sparqlEndpointPlugin.createInstance().setParameter("EndpointURL", "http://ld.opendata.cz:8894/sparql"))
        ).foreach { d =>
            d.isPublic = true
            model.dataSourceRepository.persist(d)
        }
    }
}

