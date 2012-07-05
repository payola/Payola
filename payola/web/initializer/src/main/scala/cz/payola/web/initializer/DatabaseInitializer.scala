package cz.payola.web.initializer

import cz.payola.data.PayolaDB
import cz.payola.domain.entities.plugins.concrete.data._
import cz.payola.domain.entities.plugins.concrete.query._
import cz.payola.domain.entities.plugins.concrete._
import cz.payola.data.dao._

object DatabaseInitializer extends App
{
    // Connect to the DB and (re)create the database schema.
    PayolaDB.connect()
    PayolaDB.createSchema()

    // Initialize plugins.
    val pluginDao = new PluginDAO
    val plugins = List(
        new Join,
        new OntologicalFilter,
        new ShortestPath,
        new Union,
        new PayolaStorage,
        new SparqlEndpoint,
        new ConcreteSparqlQuery,
        new Projection,
        new Selection,
        new Typed
    )
    plugins.foreach(p => pluginDao.persist(p))
}
