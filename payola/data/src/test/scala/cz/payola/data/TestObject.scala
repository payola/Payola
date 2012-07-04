package cz.payola.data

import cz.payola.data.dao._
import scala.collection.immutable
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.domain.entities.plugins.concrete._
import cz.payola.domain.entities.plugins.concrete.data.SparqlEndpoint
import cz.payola.domain.entities.plugins.concrete.query._

object TestObject
{
    // Users
    val u1 = new cz.payola.domain.entities.User("HS")
    val u2 = new cz.payola.domain.entities.User("ChM")
    val u3 = new cz.payola.domain.entities.User("JH")
    val u4 = new cz.payola.domain.entities.User("OK")
    val u5 = new cz.payola.domain.entities.User("OH")

    // Groups
    val g1 = new cz.payola.domain.entities.Group("group1", u1)
    val g2 = new cz.payola.domain.entities.Group("group2", u2)
    val g3 = new cz.payola.domain.entities.Group("group3", u3)
    val g4 = new cz.payola.domain.entities.Group("group4", u5)
    val g5 = new cz.payola.domain.entities.Group("group5", u5)

    // DAOs
    val userDao = new UserDAO
    val groupDao = new GroupDAO
    val analysisDao = new AnalysisDAO
    val plugDao = new PluginDAO
    val plugInstDao = new PluginInstanceDAO
    val dsDao = new DataSourceDAO()

    // Plugins
    val sparqlEndpointPlugin = new SparqlEndpoint
    val concreteSparqlQueryPlugin = new ConcreteSparqlQuery
    val projectionPlugin = new Projection
    val selectionPlugin = new Selection
    val typedPlugin = new Typed
    val join = new Join
    val unionPlugin = new Union

    val plugins = List(
        sparqlEndpointPlugin,
        concreteSparqlQueryPlugin,
        projectionPlugin,
        selectionPlugin,
        typedPlugin,
        join,
        unionPlugin
    )

    def connect {
        println("Connecting ...")
        PayolaDB.connect(true)
    }

    def createSchema {
        println("Creating schema ...")
        PayolaDB.createSchema();
    }

    def persistUsers {
        println("Persisting users ...")

        val user = userDao.persist(u1)
            assert(userDao.persist(u2) != null)
            assert(userDao.persist(u3) != null)
            assert(userDao.persist(u4) != null)
            assert(userDao.persist(u5) != null)

        // Update test
        user.email = "email"
        user.password = "password"
        val x = userDao.persist(user)
            assert(x.email == "email")
            assert(x.password == "password")

        val u = userDao.getById(user.id)
            assert(u != None)
            assert(u.get.id == user.id)
            assert(u.get.name == user.name)
            assert(u.get.password == user.password)
            assert(u.get.email == user.email)

        // Test userDao
            assert(userDao.findByUsername("h")(0).id == u2.id)
            assert(userDao.findByUsername("J")(0).id == u3.id)
            assert(userDao.findByUsername("K")(0).id == u4.id)
            assert(userDao.findByUsername("H").size == 3)
            assert(userDao.findByUsername(user.name)(0).id == u1.id)
            assert(userDao.findByUsername("invalid name").size == 0)
            assert(userDao.getUserByCredentials(user.name, user.password).get.id == u1.id)
            assert(userDao.getUserByCredentials("invalid", "credientals") == None)
    }

    def persistGroups {
        println("Persisting groups ...")

        val group1 = groupDao.persist(g1)
        val group2 = groupDao.persist(g2)
        val group3 = groupDao.persist(g3)
        val group4 = groupDao.persist(g4)
        val group5 = groupDao.persist(g5)

        var g = groupDao.getById(group1.id)
            assert(g != None)
            assert(g.get.id == group1.id)
            assert(g.get.name == group1.name)
            assert(g.get.owner.id == u1.id)

        g = groupDao.getById(group2.id)
            assert(g != None)
            assert(g.get.id == group2.id)
            assert(g.get.name == group2.name)
            assert(g.get.owner.id == u2.id)

        var user = userDao.getById(u1.id).get
            assert(user.ownedGroups.size == 1)
        user = userDao.getById(u4.id).get
            assert(user.ownedGroups.size == 0)
        user = userDao.getById(u5.id).get
            assert(user.ownedGroups.size == 2)
    }

    def testGroupMembership {
        val group1 = groupDao.getById(g1.id).get
        val group2 = groupDao.getById(g2.id).get
        val group3 = groupDao.getById(g3.id).get
        val group4 = groupDao.getById(g4.id).get
        val group5 = groupDao.getById(g5.id).get

        group2.addMember(userDao.getById(u1.id).get)
        group1.addMember(userDao.getById(u2.id).get)
        group1.addMember(userDao.getById(u3.id).get)
        group2.addMember(userDao.getById(u4.id).get)
        group2.addMember(userDao.getById(u5.id).get)

            assert(group1.members.size == 2)
            assert(group2.members.size == 3)
            assert(group3.members.size == 0)
            assert(group4.members.size == 0)
            assert(group5.members.size == 0)
    }

    def persistPlugins {
        println("Persisting plugins ...")

        // Persist  plugins
        for (p <- plugins) {
            val p1 = plugDao.persist(p)
                assert(p1.id == p.id)

            val p2 = plugDao.getByName(p.name).get
                assert(p1.id == p2.id)
                assert(p2.parameters.size == p.parameters.size)
                assert(p1.parameters.size == p.parameters.size)

            // assert all parameters have proper IDs
            for (param <- p2.parameters) {
                assert(p.parameters.find(_.id == param.id).get.name == param.name)
                assert(p.parameters.find(_.id == param.id).get.defaultValue == param.defaultValue)
            }

            // assert all parameters have proper IDs
            for (param <- p1.parameters) {
                assert(p.parameters.find(_.id == param.id).get.name == param.name)
                assert(p.parameters.find(_.id == param.id).get.defaultValue == param.defaultValue)
            }
        }
    }

    def persistAnalyses {
        println("Perisiting analyisis ...")
        // Persist analysis
        val user = userDao.getById(u1.id).get
        val count = analysisDao.getAll().size
        val a = new cz.payola.domain.entities.Analysis(
            "Cities with more than 2M habitants with countries " + count,
            Some(user)
        )
        a.isPublic_=(true)

        println("      defining analysis")
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

        // Try that defined analyiss can be persisted
        a.addPluginInstances(citiesFetcher, citiesTyped, citiesProjection, citiesSelection)
        a.addBinding(citiesFetcher, citiesTyped)
        a.addBinding(citiesTyped, citiesProjection)
        a.addBinding(citiesProjection, citiesSelection)

        // Persist defined analysis
        println("      persisting defined analysis")
        val analysis = analysisDao.persist(a)

            assert(analysisDao.getById(analysis.id).isDefined)
            assert(analysis.owner.get.id == user.id)
            assert(user.ownedAnalyses.size == count + 1)

            // Asset all is persisted
            assert(analysis.pluginInstances.size == a.pluginInstances.size)
            assert(analysis.pluginInstances.size > 0)
            assert(analysis.pluginInstanceBindings.size == a.pluginInstanceBindings.size)
            assert(analysis.pluginInstanceBindings.size > 0)

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

        println("      asserting persisted analysis")

        // Get analysis from DB
        val persistedAnalysis = analysisDao.getById(analysis.id).get
            assert(persistedAnalysis.pluginInstances.size == analysis.pluginInstances.size)
            assert(persistedAnalysis.pluginInstances.size == 8)
            assert(persistedAnalysis.pluginInstanceBindings.size == analysis.pluginInstanceBindings.size)
            assert(persistedAnalysis.pluginInstanceBindings.size == 7)
            assert(persistedAnalysis.owner.get.id == user.id)

        // Assert persisted plugins instances
        val pluginInstances = List(
            citiesFetcher,
            citiesTyped,
            citiesProjection,
            citiesSelection,
            countriesFetcher,
            countriesTyped,
            countriesProjection,
            citiesCountriesJoin
        )

        for (pi <- pluginInstances) {
            val pi2 = plugInstDao.getById(pi.id)
                assert(pi2.isDefined)
                assert(pi2.get.id == pi.id)
                assert(pi2.get.plugin.id == pi.plugin.id)
                assert(pi2.get.parameterValues.size == pi.parameterValues.size)

            // assert all parameters have proper IDs
            for (paramValue <- pi2.get.parameterValues) {
                assert(pi.parameterValues.find(_.id == paramValue.id).get.parameter.id == paramValue.parameter.id)
                assert(pi.parameterValues.find(_.id == paramValue.id).get.value == paramValue.value)
            }
        }
    }

    def persistDataSources {
        println("Persisting datasources")
        
        val ds1 = new DataSource("Cities", None, sparqlEndpointPlugin, immutable.Seq(
            sparqlEndpointPlugin.parameters(0).asInstanceOf[cz.payola.domain.entities.plugins.parameters.StringParameter]
                .createValue("http://dbpedia.org/ontology/Country")))
        val ds2 = new DataSource("Countries", None, sparqlEndpointPlugin, immutable.Seq(
            sparqlEndpointPlugin.parameters(0).asInstanceOf[cz.payola.domain.entities.plugins.parameters.StringParameter]
                .createValue("http://dbpedia.org/ontology/City")))
        val ds3 = new DataSource("Countries2", None, sparqlEndpointPlugin, immutable.Seq(
            sparqlEndpointPlugin.parameters(0).asInstanceOf[cz.payola.domain.entities.plugins.parameters.StringParameter]
                .createValue("http://dbpedia.org/ontology/City")))

        ds1.isPublic_=(true)
        ds2.isPublic_=(true)
        ds3.isPublic_=(true)        
        
        val ds1_db = dsDao.persist(ds1)
        val ds2_db = dsDao.persist(ds2)
        val ds3_db = dsDao.persist(ds3)

            assert(ds1.id == ds1_db.id)
            assert(ds2.id == ds2_db.id)
            assert(ds3.id == ds3_db.id)

            assert(ds1.parameterValues.size == ds1_db.parameterValues.size)
            assert(ds2.parameterValues.size == ds2_db.parameterValues.size)
            assert(ds3.parameterValues.size == ds3_db.parameterValues.size)

        //println("DSs: " + dsDao.getPublicDataSources().size)
            assert(dsDao.getPublicDataSources().size == 0)
    }


    def testPagination {
        println("Pagination ...")
            assert(userDao.getAll(Some(new PaginationInfo(2,1))).size == 1)
            assert(userDao.getAll(Some(new PaginationInfo(2,4))).size == 3)
            assert(userDao.getAll(Some(new PaginationInfo(5,1))).size == 0)
            assert(userDao.getAll(Some(new PaginationInfo(4,0))).size == 0)

            assert(groupDao.getAll().size == 5)
            assert(groupDao.getAll(Some(new PaginationInfo(1, 2))).size == 2)
            assert(groupDao.getAll(Some(new PaginationInfo(2, 5))).size == 3)
            assert(groupDao.getAll(Some(new PaginationInfo(5, 1))).size == 0)
            assert(groupDao.getAll(Some(new PaginationInfo(4, 0))).size == 0)
    }

    def testCascadeDeletes {

        var analysisCount = analysisDao.getAll().size
        var pluginInstancesCount = plugInstDao.getAll().size
        var pluginsCount = plugDao.getAll().size

        // Create second analysis in DB
        persistAnalyses

        assert(analysisDao.getAll().size == analysisCount + 1)
        assert(plugInstDao.getAll().size == pluginInstancesCount * 2)
        assert(plugDao.getAll().size == pluginsCount)

        // Remove one analysis
        assert(analysisDao.removeById(analysisDao.getAll()(0).id) == true)

        // One analysis and half of plugin instances are gone
        assert(analysisDao.getAll().size == analysisCount)
        assert(plugInstDao.getAll().size == pluginInstancesCount)
        assert(plugDao.getAll().size == pluginsCount)

        val analysis = analysisDao.getAll()(0)

        // Remove all plugins
        for (p <- plugins) {
            assert(plugDao.removeById(p.id) == true)
        }

        // Only (empty) analysis is left
        assert(analysisDao.getAll().size == analysisCount)
        assert(plugInstDao.getAll().size == 0)
        assert(plugDao.getAll().size == 0)

        assert(analysis.pluginInstances.size == 0)
        assert(analysis.pluginInstanceBindings.size == 0)
    }
}
