package cz.payola.data.entities

import dao._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.data.entities.analyses._
import cz.payola.data.entities.analyses.parameters._
import cz.payola.domain.entities.analyses.plugins.data.SparqlEndpoint
import cz.payola.domain.entities.analyses.plugins.query._
import cz.payola.domain.entities.analyses.plugins._

class SquerylSpecs extends FlatSpec with ShouldMatchers
{
    val userDao = new UserDAO()

    val groupDao = new GroupDAO()

    val analysisDao = new AnalysisDAO()

    val plugDao = new PluginDAO()

    val plugInstDao = new PluginInstanceDAO()

    val bParDao = new BooleanParameterDAO()

    val bParInstDao = new BooleanParameterInstanceDAO()

    val fParDao = new FloatParameterDAO()

    val fParInstDao = new FloatParameterInstanceDAO()

    val iParDao = new IntParameterDAO()

    val iParInstDao = new IntParameterInstanceDAO()

    val sParDao = new StringParameterDAO()

    val sParInstDao = new StringParameterInstanceDAO()

    val user = new User("name", "pwd1", "email1")

    val group1 = new Group("group", user)

    val group2 = new Group("group2", user)

    val bPar = new BooleanParameter("bPar", "bPar", true)

    val bParInst = new BooleanParameterValue("bParVal", bPar, false)

    val fPar = new FloatParameter("fPar", "fPar", -1.0f)

    val fParInst = new FloatParameterValue("fParVal", fPar, 1.0f)

    val iPar = new IntParameter("iParVal", "iPar", -1)

    val iParInst = new IntParameterValue("iParVal", iPar, 1)

    val sPar = new StringParameter("sPar", "sPar", "empty")

    val sParInst = new StringParameterValue("sParVal", sPar, "string")

    // Init
    assert (PayolaDB.connect())

    "Database" should "be created succesfuly" in {
        PayolaDB.createSchema()
    }

    "Users" should "be persited, loaded and managed by UserDAO" in {
        userDao.persist(user)

        // Update test
        user.name += "1"
        userDao.persist(user)

        val u = userDao.getById(user.id)
        assert(u != None)
        assert(u.get.id == user.id)
        assert(u.get.name == user.name)
        assert(u.get.password == user.password)
        assert(u.get.email == user.email)

        // Test userDao
        assert(userDao.findByUsername("n", 0, 1)(0).id == user.id)
        assert(userDao.findByUsername("a", 0, 1)(0).id == user.id)
        assert(userDao.findByUsername("1", 0, 1)(0).id == user.id)
        assert(userDao.findByUsername(user.name, 0, 1)(0).id == user.id)
        assert(userDao.findByUsername("invalid name").size == 0)
        assert(userDao.getUserByCredentials(user.name, user.password).get.id == user.id)
        assert(userDao.getUserByCredentials("invalid", "credientals") == None)
    }

    "Groups" should "be persisted, loaded and managed by GroupDAO" in {
        groupDao.persist(group1)
        groupDao.persist(group2)

        // Update test
        group1.name += "1"
        groupDao.persist(group1)

        val g = groupDao.getById(group1.id)
        assert(g != None)
        assert(g.get.id == group1.id)
        assert(g.get.name == group1.name)
        assert(g.get.ownerId.get == user.id)
    }

    "Members of groups" should "be persisted" in {
        group1.addMember(user)
        group2.addMember(user)

        assert(user.memberGroups.size == 2)
        assert(user.ownedGroups.size == 2)

        assert(group1.members(0).name == user.name, "Invalid group owner")
        assert(group2.members(0).name == user.name, "Invalid group2 owner")
    }

    "BooleanParameters" should "be persited, loaded and managed by BooleanParameterDAO" in {
        bParDao.persist(bPar)

        val p = bParDao.getById(bPar.id)
        assert(p != None)
        assert(p.get.id == bPar.id)

        val x = bParDao.getById("")
        assert(x == None)
    }

    "BooleanParameterInstances" should "be persited, loaded and managed by BooleanParameterInstanceDAO" in {
        bParInstDao.persist(bParInst)

        val p = bParInstDao.getById(bParInst.id)
        assert(p != None)
        assert(p.get.id == bParInst.id)

        val x = bParInstDao.getById("")
        assert(x == None)

        assert(bPar.parameterValues.size == 1)
    }

    "FloatParameters" should "be persited, loaded and managed by FloatParameterDAO" in {
        fParDao.persist(fPar)

        val p = fParDao.getById(fPar.id)
        assert(p != None)
        assert(p.get.id == fPar.id)

        val x = fParDao.getById("")
        assert(x == None)
    }

    "FloatParameterInstances" should "be persited, loaded and managed by FloatParameterInstanceDAO" in {
        fParInstDao.persist(fParInst)

        val p = fParInstDao.getById(fParInst.id)
        assert(p != None)
        assert(p.get.id == fParInst.id)

        val x = fParInstDao.getById("")
        assert(x == None)

        assert(fPar.parameterValues.size == 1)
    }

    "IntParameters" should "be persited, loaded and managed by IntParameterDAO" in {
        iParDao.persist(iPar)

        val p = iParDao.getById(iPar.id)
        assert(p != None)
        assert(p.get.id == iPar.id)

        val x = iParDao.getById("")
        assert(x == None)
    }

    "IntParameterInstances" should "be persited, loaded and managed by IntParameterInstanceDAO" in {
        iParInstDao.persist(iParInst)

        val p = iParInstDao.getById(iParInst.id)
        assert(p != None)
        assert(p.get.id == iParInst.id)

        val x = iParInstDao.getById("")
        assert(x == None)

        assert(iPar.parameterValues.size == 1)
    }

    "StringParameters" should "be persited, loaded and managed by StringParameterDAO" in {
        sParDao.persist(sPar)

        val p = sParDao.getById(sPar.id)
        assert(p != None)
        assert(p.get.id == sPar.id)

        val x = sParDao.getById("")
        assert(x == None)
    }

    "StringParameterInstances" should "be persited, loaded and managed by StringParameterInstanceDAO" in {
        sParInstDao.persist(sParInst)

        val p = sParInstDao.getById(sParInst.id)
        assert(p != None)
        assert(p.get.id == sParInst.id)

        val x = sParInstDao.getById("")
        assert(x == None)

        assert(sPar.parameterValues.size == 1)
    }
    "Analysis evaluation" should "work" in {
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

        // TODO: this analysis is from data, not domain. OK?

        // persist analysis
        val analysis = new Analysis("Cities with more than 2 million habitants with countries", None)
        analysisDao.persist(analysis)
        assert(analysisDao.getById(analysis.id).isDefined)

        // Persist  plugins
        for (p <- plugins) {
            plugDao.persist(p)

            val p2 = plugDao.getByName(p.name)
            assert(p2.isDefined)
            assert(p2.get.id == p.id)
            assert(p2.get.parameters.size == p.parameters.size)

            // assert all parameters have proper IDs
            for(param <- p2.get.parameters){
                assert(p.parameters.find(_.id == param.id).get.name == param.name)
                assert(p.parameters.find(_.id == param.id).get.defaultValue == param.defaultValue)
            }
        }

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

        analysisDao.persist(analysis)

        // Get analysis from DB
        val persistedAnalysis = analysisDao.getById(analysis.id)
        assert(persistedAnalysis.isDefined)
        assert(persistedAnalysis.get.pluginInstances.size > 0)
        assert(persistedAnalysis.get.pluginInstanceBindings.size > 0)
        assert(persistedAnalysis.get.pluginInstanceInputBindings.size > 0)
        assert(persistedAnalysis.get.pluginInstanceOutputBindings.size > 0)

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

        for(pi <- pluginInstances) {
            val pi2 = plugInstDao.getById(pi.id)
            assert(pi2.isDefined)
            assert(pi2.get.id == pi.id)
            assert(pi2.get.parameterValues.size == pi.parameterValues.size)

            // assert all parameters have proper IDs
            for(paramValue <- pi2.get.parameterValues){
                assert(pi.parameterValues.find(_.id == paramValue.id).get.parameter.id == paramValue.parameter.id)
                assert(pi.parameterValues.find(_.id == paramValue.id).get.value == paramValue.value)
            }
        }

        /*
        // .. and test it
        val evaluation = persistedAnalysis.get.evaluate()
        while (!evaluation.isFinished) {
            println("Not finished, current progress: " + evaluation.progress.value)
            Thread.sleep(1000)
        }
        val result = evaluation.result

        println("Done with result: " + result.toString)
        assert(result.map(_.isInstanceOf[Success]).getOrElse(false))
        */

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

    "Cascade Deletes" should "be used when defined" in {
        // With parameter its instance should be deleted        
        bParDao.removeById(bPar.id)
        assert(bParDao.getById(bPar.id) == None)
        assert(bParInstDao.getById(bParInst.id) == None)

        // Parameter instance removing should not remove parameter
        fParInstDao.removeById(fParInst.id)
        assert(fParDao.getById(fPar.id) != None)
        assert(fParInstDao.getById(fParInst.id) == None)
    }

    "DAOs" should "paginate properly" in {
        assert(userDao.getAll().size == 1)
        assert(groupDao.getAll().size == 2)
        assert(groupDao.getAll(1, 2).size == 1)
        assert(groupDao.getAll(2, 5).size == 0)
        assert(groupDao.getAll(1, 0).size == 0)
    }
}
