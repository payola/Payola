package cz.payola.data.entities

import dao._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.data.entities.analyses._
import cz.payola.data.entities.analyses.parameters._
import scala.collection.immutable
import cz.payola.domain.entities.analyses.plugins.data.SparqlEndpoint
import cz.payola.domain.entities.analyses.plugins.query._
import cz.payola.domain.entities.analyses.plugins.Union

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

    val analysis = new Analysis("an", Some(user))

    val bPar = new BooleanParameter("bPar", true)

    val bParInst = new BooleanParameterValue(bPar, false)

    val fPar = new FloatParameter("fPar", -1.0f)

    val fParInst = new FloatParameterValue(fPar, 1.0f)

    val iPar = new IntParameter("iPar", -1)

    val iParInst = new IntParameterValue(iPar, 1)

    val sPar = new StringParameter("sPar", "empty")

    val sParInst = new StringParameterValue(sPar, "string")

    val plug = new Plugin("plugin", 1, List(bPar, fPar, iPar, sPar))

    val plugInst = new PluginInstance(plug, List(bParInst, fParInst, iParInst, sParInst))

    // Init
    assert (PayolaDB.connect())

    "Database" should "be created succesfuly" in {
        PayolaDB.createSchema()
    }

    "1) Users" should "be persited, loaded and managed by UserDAO" in {
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

    "2) Groups" should "be persisted, loaded and managed by GroupDAO" in {
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

    "3) Members of groups" should "be persisted" in {
        user.addToGroup(group1)
        group2.addMember(user)

        assert(user.memberGroups.size == 2)
        assert(user.ownedGroups.size == 2)

        assert(group1.members(0).name == user.name, "Invalid group owner")
        assert(group2.members(0).name == user.name, "Invalid group2 owner")
    }

    "4) Analyses" should "be persited, loaded and managed by AnalysesDAO" in {
        analysisDao.persist(analysis)

        // Update test
        analysis.name += "1"
        analysisDao.persist(analysis)

        assert(user.ownedAnalyses.size == 1)

        val a = analysisDao.getById(analysis.id)
        assert(a != None)
        assert(a.get.id == analysis.id)

        val x = analysisDao.getById("")
        assert(x == None)
    }

    "5) Plugins" should "be persited, loaded and managed by PluginsDAO" in {
        plugDao.persist(plug)

        // Update test
        plug.name += "1"
        plugDao.persist(plug)

        val p = plugDao.getById(plug.id)
        assert(p != None)
        assert(p.get.name == plug.name)
        assert(p.get.id == plug.id)

        val x = plugDao.getById("")
        assert(x == None)
    }

    "6) PluginInstances" should "be persited, loaded and managed by PluginInstancesDAO" in {
        plugInst.analysisId = Some(analysis.id)
        plugInstDao.persist(plugInst)

        val p = plugInstDao.getById(plugInst.id)
        assert(p != None)
        assert(p.get.id == plugInst.id)

        val x = plugInstDao.getById("")
        assert(x == None)
    }

    "7) BooleanParameters" should "be persited, loaded and managed by BooleanParameterDAO" in {
        bParDao.persist(bPar)

        val p = bParDao.getById(bPar.id)
        assert(p != None)
        assert(p.get.id == bPar.id)

        val x = bParDao.getById("")
        assert(x == None)
    }

    "8) BooleanParameterInstances" should "be persited, loaded and managed by BooleanParameterInstanceDAO" in {
        bParInstDao.persist(bParInst)

        val p = bParInstDao.getById(bParInst.id)
        assert(p != None)
        assert(p.get.id == bParInst.id)

        val x = bParInstDao.getById("")
        assert(x == None)

        assert(bPar.instances.size == 1)
    }

    "9) FloatParameters" should "be persited, loaded and managed by FloatParameterDAO" in {
        fParDao.persist(fPar)

        val p = fParDao.getById(fPar.id)
        assert(p != None)
        assert(p.get.id == fPar.id)

        val x = fParDao.getById("")
        assert(x == None)
    }

    "10) FloatParameterInstances" should "be persited, loaded and managed by FloatParameterInstanceDAO" in {
        fParInstDao.persist(fParInst)

        val p = fParInstDao.getById(fParInst.id)
        assert(p != None)
        assert(p.get.id == fParInst.id)

        val x = fParInstDao.getById("")
        assert(x == None)

        assert(fPar.instances.size == 1)
    }

    "11) IntParameters" should "be persited, loaded and managed by IntParameterDAO" in {
        iParDao.persist(iPar)

        val p = iParDao.getById(iPar.id)
        assert(p != None)
        assert(p.get.id == iPar.id)

        val x = iParDao.getById("")
        assert(x == None)
    }

    "12) IntParameterInstances" should "be persited, loaded and managed by IntParameterInstanceDAO" in {
        iParInstDao.persist(iParInst)

        val p = iParInstDao.getById(iParInst.id)
        assert(p != None)
        assert(p.get.id == iParInst.id)

        val x = iParInstDao.getById("")
        assert(x == None)

        assert(iPar.instances.size == 1)
    }

    "13) StringParameters" should "be persited, loaded and managed by StringParameterDAO" in {
        sParDao.persist(sPar)

        val p = sParDao.getById(sPar.id)
        assert(p != None)
        assert(p.get.id == sPar.id)

        val x = sParDao.getById("")
        assert(x == None)
    }

    "14) StringParameterInstances" should "be persited, loaded and managed by StringParameterInstanceDAO" in {
        sParInstDao.persist(sParInst)

        val p = sParInstDao.getById(sParInst.id)
        assert(p != None)
        assert(p.get.id == sParInst.id)

        val x = sParInstDao.getById("")
        assert(x == None)

        assert(sPar.instances.size == 1)
    }

    "15) Plugins and Parameters" should "work with their instances" in {
        assert(plug.parameters.size == 4)
        assert(plugInst.parameterValues.size == 4)

        assert(plug.parameters.find(par => par.id == bPar.id) != None)
        assert(plug.parameters.find(par => par.id == fPar.id) != None)
        assert(plug.parameters.find(par => par.id == iPar.id) != None)
        assert(plug.parameters.find(par => par.id == sPar.id) != None)

        assert(plugInst.parameterValues.find(par => par.id == bParInst.id) != None)
        assert(plugInst.parameterValues.find(par => par.id == fParInst.id) != None)
        assert(plugInst.parameterValues.find(par => par.id == iParInst.id) != None)
        assert(plugInst.parameterValues.find(par => par.id == sParInst.id) != None)
    }

    "16) Cascade Deletes" should "be used when defined" in {
        // With parameter its instance should be deleted        
        bParDao.removeById(bPar.id)
        assert(bParDao.getById(bPar.id) == None)
        assert(bParInstDao.getById(bParInst.id) == None)
        assert(plug.parameters.size == 3)
        assert(plugInst.parameterValues.size == 3)

        // Parameter instance removing should not remove parameter 
        fParInstDao.removeById(fParInst.id)
        assert(fParDao.getById(fPar.id) != None)
        assert(fParInstDao.getById(fParInst.id) == None)
        assert(plug.parameters.size == 3)
        assert(plugInst.parameterValues.size == 2)

        // Remove plugin -> remove parameters, plugin instances, parameter instances
        plugDao.removeById(plug.id)
        assert(analysis.pluginInstances.size == 0)
        assert(plugInstDao.getById(plugInst.id) == None)
        assert(sParDao.getById(sPar.id) == None)
        assert(sParInstDao.getById(sParInst.id) == None)

        // Prepare for analysis removal test
        plugDao.persist(plug)
        sParDao.persist(sPar)
        plugInstDao.persist(plugInst)
        sParInstDao.persist(sParInst)
        assert (analysis.pluginInstances.size == 1)
        assert (plugInst.parameterValues.find(par => par.id == sParInst.id) != None)

        // Remove analysis -> remove plugin instances
        analysisDao.removeById(analysis.id)
        assert (plugInstDao.getById(plugInst.id) == None)
    }

    /*
    "Analysis" should "work" in {
        val sparqlEndpointPlugin = new SparqlEndpoint
        val concreteSparqlQueryPlugin = new ConcreteSparqlQuery
        val projectionPlugin = new Projection
        val selectionPlugin = new Selection
        val typedPlugin = new Typed
        //TODO: missing LeftJoin class in: val leftJoinPlugin = new LeftJoin
        val unionPlugin = new Union

        val plugins = List(
            sparqlEndpointPlugin,
            concreteSparqlQueryPlugin,
            projectionPlugin,
            selectionPlugin,
            typedPlugin,
            //leftJoinPlugin,
            unionPlugin
        )

        for (p <- plugins) {
            plugDao.persist(new Plugin(p.name, p.inputCount, p.parameters))
            assert(plugDao.getByName(p.name) != None)
        }
        
        val plugInst2 = plug.createInstance()
        analysis.addPluginInstances(plugInst, plugInst2)
        analysis.addBinding(new PluginInstanceBinding(plugInst, plugInst2))

        assert(analysis.pluginInstanceBindings.size == 1)
    }
    */

    "DAOs" should "paginate properly" in {
        assert(userDao.getAll().size == 1)
        assert(groupDao.getAll().size == 2)
        assert(groupDao.getAll(1, 2).size == 1)
        assert(groupDao.getAll(2, 5).size == 0)
        assert(groupDao.getAll(1, 0).size == 0)
    }
}
