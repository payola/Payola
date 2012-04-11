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
    val SParInstDao = new StringParameterInstanceDAO()

    val user = new User("u1", "name", "pwd1", "email1")
    val group1 = new Group("g1", "group", user)
    val group2 = new Group("g2", "group2", user)
    val analysis = new Analysis("a1", "an1", user)
    val plugin = new Plugin("p1", "plugin1")
    val pluginInstance = new PluginInstance("pi1", plugin)

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

        group1.name += "1"
        groupDao.persist(group1)
        
        val g = groupDao.getById(group1.id)
        assert(g != None)
        assert(g.get.id == group1.id)
        assert(g.get.name == group1.name)
        //TOOD: assert(g.get.ownerId == user.id)
    }

    "3) Members of groups" should "be persisted" in {
        user.becomeMemberOf(group1)
        group2.addMember(user)

        assert(user.memberedGroups2.size == 2)
        assert(user.ownedGroups2.size == 2)

        assert(group1.groupMembers2(0).name == user.name, "Invalid group owner")
        assert(group2.groupMembers2(0).name == user.name, "Invalid group2 owner")
    }

    "4) Analyses" should "be persited, loaded and managed by AnalysesDAO" in {
        analysisDao.persist(analysis)

        assert(user.ownedAnalyses2.size == 1)

        val a = analysisDao.getById(analysis.id)
        assert (a != None)
        assert (a.get.id == analysis.id)

        val x = analysisDao.getById("")
        assert (x == None)
    }

    "5) Plugins" should "be persited, loaded and managed by PluginsDAO" in {
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
    }

    "8) BooleanParameterInstances" should "be persited, loaded and managed by BooleanParameterInstanceDAO" in {
    }
}
