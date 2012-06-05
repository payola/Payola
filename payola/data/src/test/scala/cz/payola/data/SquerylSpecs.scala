package cz.payola.data

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.data.entities.analyses.parameters._
import cz.payola.domain.entities.analyses.plugins.data.SparqlEndpoint
import cz.payola.domain.entities.analyses.plugins.query._
import cz.payola.domain.entities.analyses.plugins._
import cz.payola.data.dao._
import cz.payola.data.entities._

class SquerylSpecs extends FlatSpec with ShouldMatchers
{
    // Init
    assert(PayolaDB.connect())

    PayolaDB.createSchema()

    "Users" should "be persited, loaded and managed by UserDAO" in {

        val userDao = new UserDAO()

        val u1 = new cz.payola.domain.entities.User("name")

        println("Persisting user ...")
        val user = userDao.persist(u1).get

        // Update test
        user.email = "email"
        user.password = "password"
        val x = userDao.persist(user).get
        assert(x.email == "email")
        assert(x.password == "password")

        val u = userDao.getById(user.id)
        assert(u != None)
        assert(u.get.id == user.id)
        assert(u.get.name == user.name)
        assert(u.get.password == user.password)
        assert(u.get.email == user.email)

        // Test userDao
        assert(userDao.findByUsername("n", 0, 1)(0).id == user.id)
        assert(userDao.findByUsername("a", 0, 1)(0).id == user.id)
        assert(userDao.findByUsername(user.name, 0, 1)(0).id == user.id)
        assert(userDao.findByUsername("invalid name").size == 0)
        assert(userDao.getUserByCredentials(user.name, user.password).get.id == user.id)
        assert(userDao.getUserByCredentials("invalid", "credientals") == None)
    }

    "Groups" should "be persisted, loaded and managed by GroupDAO" in {
        val groupDao = new GroupDAO()

        val user = new UserDAO().findByUsername("n", 0, 1)(0)
        val g1 = new cz.payola.domain.entities.Group("group1", user)
        val g2 = new cz.payola.domain.entities.Group("group2", user)
        val group1 = groupDao.persist(g1).get
        val group2 = groupDao.persist(g2).get

        val g = groupDao.getById(group1.id)
        assert(g != None)
        assert(g.get.id == group1.id)
        assert(g.get.name == group1.name)
        assert(g.get.ownerId.get == user.id)

        //TODO: returns 4 not 2 - assert(user.ownedGroups.size == 2)
    }

    "Analysis persistance" should "work" in {
        val sparqlEndpointPlugin = new SparqlEndpoint
        val concreteSparqlQueryPlugin = new ConcreteSparqlQuery
        val projectionPlugin = new Projection
        val selectionPlugin = new Selection
        val typedPlugin = new Typed
        val join = new Join
        val unionPlugin = new Union

        val analysisDao = new AnalysisDAO
        val plugDao = new PluginDAO
        val plugInstDao = new PluginInstanceDAO

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
        val a = new cz.payola.domain.entities.Analysis("Cities with more than 2 million habitants with countries", None)
        val analysis = analysisDao.persist(a).get
        assert(analysisDao.getById(analysis.id).isDefined)

        // Persist  plugins
        for (p <- plugins) {
            plugDao.persist(p)

            val p2 = plugDao.getByName(p.name)
            assert(p2.isDefined)
            assert(p2.get.id == p.id)
            assert(p2.get.parameters.size == p.parameters.size)

            // assert all parameters have proper IDs
            for (param <- p2.get.parameters) {
                assert(p.parameters.find(_.id == param.id).get.name == param.name)
                assert(p.parameters.find(_.id == param.id).get.defaultValue == param.defaultValue)
            }
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

        // Get analysis from DB
        val persistedAnalysis = analysisDao.getById(analysis.id).get
        assert(persistedAnalysis.pluginInstances.size == analysis.pluginInstances.size)
        assert(persistedAnalysis.pluginInstanceBindings.size == analysis.pluginInstanceBindings.size)

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
            assert(pi2.get.parameterValues.size == pi.parameterValues.size)

            // assert all parameters have proper IDs
            for (paramValue <- pi2.get.parameterValues) {
                assert(pi.parameterValues.find(_.id == paramValue.id).get.parameter.id == paramValue.parameter.id)
                assert(pi.parameterValues.find(_.id == paramValue.id).get.value == paramValue.value)
            }
        }

        /*
        // TODO: Test : Remove plugin -> remove parameters, plugin parameterValues, parameter parameterValues
        plugDao.removeById(sparqlEndpointPlugin.id)
        assert(analysis.pluginInstances.size == 0)
        assert(plugInstDao.getById(citiesFetcher.id) == None)

        // Prepare for analysis removal test
        plugDao.persist(sparqlEndpointPlugin)
        sParDao.persist(sPar)
        plugInstDao.persist(plugInst)
        sParInstDao.persist(sParInst)
        assert (analysis.pluginInstances.size == 1)
        assert (plugInst.parameterValues.find(par => par.id == sParInst.id) != None)

        // Remove analysis -> remove plugin parameterValues
        analysisDao.removeById(analysis.id)
        assert (plugInstDao.getById(plugInst.id) == None)
        */
    }

    "DAOs" should "paginate properly" in {
        val userDao = new UserDAO()
        val groupDao = new GroupDAO()

        assert(userDao.getAll().size == 1)
        assert(groupDao.getAll().size == 2)
        assert(groupDao.getAll(1, 2).size == 1)
        assert(groupDao.getAll(2, 5).size == 0)
        assert(groupDao.getAll(1, 0).size == 0)
    }
}
