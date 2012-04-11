package cz.payola.data.entities

import dao._
import org.squeryl.PrimitiveTypeMode._
import schema.PayolaDB

object TestObject
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

    def main(args: Array[String]) = {
        println("1")
        PayolaDB.startDatabaseSession()

        println("2")
        PayolaDB.createSchema()

        println("3")
        val user = new User("u1", "name1", "pwd1", "email1")
        userDao.persist(user)

        user.name += "1"
        userDao.persist(user)

        println("4")
        val group = new Group("g1", "group1", user)
        groupDao.persist(group)

        val group2 = new Group("g2", "group2", user)
        groupDao.persist(group2)

        val analysis = new Analysis("a1", "an1", user)
        analysisDao.persist(analysis)

        val plugin = new Plugin("p1", "plugin1")
        pluginDao.persist(plugin)

        val pluginInstance = new PluginInstance("pi1", plugin)
        piDao.persist(pluginInstance)

        user.becomeMemberOf(group)
        group2.addMember(user)

        for (a <- user.memberedGroups2) {
            println(a.name)
        }

        for (g <- user.ownedGroups2) {
            println(g.name)
        }

        for (a <- user.ownedAnalyses2) {
            println(a.name)
        }

        // Validate saved values

        // Test userDao
        assert(userDao.findByUsername(user.name, 0, 1)(0).id == user.id)
        assert(userDao.findByUsername("invalid name").size == 0)
        assert(userDao.getUserByCredentials(user.name, user.password).get.id == user.id)
        assert(userDao.getUserByCredentials("invalid", "credientals") == None)

        assert(user.memberedGroups2.size == 2)
        assert(user.ownedGroups2.size == 2)
        assert(user.ownedAnalyses2.size == 1)

        assert(group.groupMembers2(0).name == user.name, "Invalid group owner")
        assert(group2.groupMembers2(0).name == user.name, "Invalid group2 owner")

        val u = userDao.getById(user.id)
        assert(u != None)
        assert(u.get.name == user.name)

        val g = groupDao.getById(group.id)
        assert(g != None)
        assert(g.get.name == group.name)

        val a = analysisDao.getById(analysis.id)
        assert(a != None)
        assert(a.get.name == analysis.name)

        val p = pluginDao.getById(plugin.id)
        assert(p != None)
        assert(p.get.name == plugin.name)

        val pi = piDao.getById(pluginInstance.id)
        assert(pi != None)
    }
}
