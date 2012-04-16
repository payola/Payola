package cz.payola.data.entities

import dao._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import schema.PayolaDB

class SquerylSpecs extends FlatSpec with ShouldMatchers
{
    val userDao = new UserDAO()
    val groupDao = new GroupDAO()
    val analysisDao = new AnalysisDAO()
    val pluginDao = new PluginDAO
    val piDao = new PluginInstanceDAO()
    val bParDao = new BooleanParameterDAO()
    val bParInstDao = new BooleanParameterInstanceDAO()
    val fParDao = new FloatParameterDAO()
    val fParInstDao = new FloatParameterInstanceDAO()
    val iParDao = new IntParameterDAO()
    val iParInstDao = new IntParameterInstanceDAO()
    val sParDao = new StringParameterDAO()
    val sParInstDao = new StringParameterInstanceDAO()

    val user = new User("u1", "name", "pwd1", "email1")
    val group1 = new Group("g1", "group", user)
    val group2 = new Group("g2", "group2", user)
    val analysis = new Analysis("a1", "an", user)
    val plugin = new Plugin("p1", "plugin")
    val pluginInstance = new PluginInstance("pi1", plugin)
    val bPar = new BooleanParameter("b1", "bPar", true)
    val bParInst = new BooleanParameterInstance("bi1", bPar, false)
    val fPar = new FloatParameter("b1", "fPar", -1.0f)
    val fParInst = new FloatParameterInstance("fi1", fPar, 1.0f)
    val iPar = new IntParameter("b1", "iPar", -1)
    val iParInst = new IntParameterInstance("ii1", iPar, 1)
    val sPar = new StringParameter("b1", "sPar", "empty")
    val sParInst = new StringParameterInstance("si1", sPar, "string")

    // Init
    PayolaDB.startDatabaseSession()

    "Database" should "be created succesfuly" in {
        PayolaDB.createSchema()
    }

    "1) Users" should "be persited, loaded and managed by UserDAO" in {
        userDao.persist(user)

        // Update test
        user.name += "1"
        userDao.persist(user)

        val u = userDao.getById(user.id)
        assert (u != None)
        assert (u.get.id == user.id)
        assert (u.get.name == user.name)
        assert (u.get.password == user.password)
        assert (u.get.email == user.email)

        // Test userDao
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
        assert(g.get.ownerId == user.id)
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
        assert (a != None)
        assert (a.get.id == analysis.id)

        val x = analysisDao.getById("")
        assert (x == None)
    }

    "5) Plugins" should "be persited, loaded and managed by PluginsDAO" in {
        pluginDao.persist(plugin)

        // Update test
        plugin.name += "1"
        pluginDao.persist(plugin)

        val p = pluginDao.getById(plugin.id)
        assert (p != None)
        assert (p.get.name == plugin.name)
        assert (p.get.id == plugin.id)

        val x = pluginDao.getById("")
        assert (x == None)
    }

    "6) PluginInstances" should "be persited, loaded and managed by PluginInstancesDAO" in {
        piDao.persist(pluginInstance)

        val p = piDao.getById(pluginInstance.id)
        assert (p != None)
        assert (p.get.id == pluginInstance.id)

        val x = piDao.getById("")
        assert (x == None)
    }

    "7) BooleanParameters" should "be persited, loaded and managed by BooleanParameterDAO" in {
        bParDao.persist(bPar)

        // Update test
        bPar.name += "1"
        bParDao.persist(bPar)
        
        val p = bParDao.getById(bPar.id)
        assert (p != None)
        assert (p.get.id == bPar.id)

        val x = bParDao.getById("")
        assert (x == None)
    }

    "8) BooleanParameterInstances" should "be persited, loaded and managed by BooleanParameterInstanceDAO" in {
        bParInstDao.persist(bParInst)

        val p = bParInstDao.getById(bParInst.id)
        assert (p != None)
        assert (p.get.id == bParInst.id)

        val x = bParInstDao.getById("")
        assert (x == None)
    }

    "9) FloatParameters" should "be persited, loaded and managed by FloatParameterDAO" in {
        fParDao.persist(fPar)

        // Update test
        fPar.name += "1"
        fParDao.persist(fPar)

        val p = fParDao.getById(fPar.id)
        assert (p != None)
        assert (p.get.id == fPar.id)

        val x = fParDao.getById("")
        assert (x == None)
    }

    "10) FloatParameterInstances" should "be persited, loaded and managed by FloatParameterInstanceDAO" in {
        fParInstDao.persist(fParInst)

        val p = fParInstDao.getById(fParInst.id)
        assert (p != None)
        assert (p.get.id == fParInst.id)

        val x = fParInstDao.getById("")
        assert (x == None)
    }

    "11) IntParameters" should "be persited, loaded and managed by IntParameterDAO" in {
        iParDao.persist(iPar)

        // Update test
        iPar.name += "1"
        iParDao.persist(iPar)

        val p = iParDao.getById(iPar.id)
        assert (p != None)
        assert (p.get.id == iPar.id)

        val x = iParDao.getById("")
        assert (x == None)
    }

    "12) IntParameterInstances" should "be persited, loaded and managed by IntParameterInstanceDAO" in {
        iParInstDao.persist(iParInst)

        val p = iParInstDao.getById(iParInst.id)
        assert (p != None)
        assert (p.get.id == iParInst.id)

        val x = iParInstDao.getById("")
        assert (x == None)
    }

    "13) StringParameters" should "be persited, loaded and managed by StringParameterDAO" in {
        bParDao.persist(bPar)

        // Update test
        bPar.name += "1"
        bParDao.persist(bPar)

        val p = bParDao.getById(bPar.id)
        assert (p != None)
        assert (p.get.id == bPar.id)

        val x = bParDao.getById("")
        assert (x == None)
    }

    "14) StringParameterInstances" should "be persited, loaded and managed by StringParameterInstanceDAO" in {
        sParInstDao.persist(sParInst)

        val p = sParInstDao.getById(sParInst.id)
        assert (p != None)
        assert (p.get.id == sParInst.id)

        val x = sParInstDao.getById("")
        assert (x == None)
    }

    "DAOs" should "paginate properly" in {
        assert(userDao.getAll().size == 1)
        assert(groupDao.getAll().size == 2)
        assert(groupDao.getAll(1,2).size == 1)
        assert(groupDao.getAll(2,5).size == 0)
        assert(groupDao.getAll(1,0).size == 0)
    }
}
