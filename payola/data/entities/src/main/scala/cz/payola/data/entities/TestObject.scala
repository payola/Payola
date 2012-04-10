package cz.payola.data.entities

import dao.UserDAO
import org.squeryl.PrimitiveTypeMode._
import PayolaDB._

object TestObject
{
    val userDao = new UserDAO()

    def main(args: Array[String]) = {
        println("1")
        PayolaDB.startDatabaseSession()

        println("2")
        PayolaDB.createSchema()

        println("3")
        val user = new User("u1", "name1", "pwd1", "email1")

        //PayolaDB.save(user);
        //userDao.persist(user)
        user.persist

        user.name += "1"
        user.persist

        println("4")
        val group = new Group("g1", "group1", user)
        //PayolaDB.save(group)
        group.persist

        val group2 = new Group("g2", "group2", user)
        //PayolaDB.save(group2)
        //group2.save
        group2.persist

        val analysis = new Analysis("a1", "an1", user)
        analysis.save

        val plugin = new Plugin("p1", "plugin1")
        plugin.save

        val pluginInstance = new PluginInstance("pi1", plugin)
        pluginInstance.save

        transaction {
            user.memberedGroups.associate(group)
            user.memberedGroups.associate(group2)

            for (g <- user.ownedGroups2) {
                println(g.name)
            }

            for (a <- user.ownedAnalyses2) {
                println(a.name)
            }

            // Validate saved values
            assert(user.memberedGroups.size == 2)
            assert(user.ownedGroups2.size == 2)
            assert(user.ownedAnalyses2.size == 1)

            assert(group.members2.single.name == user.name, "Invalid group owner")
            assert(group2.members2.single.name == user.name, "Invalid group2 owner")

            val u = userDao.getById(user.id)
            assert(u != None)
            assert(u.get.name == user.name)

            /*
            val g = PayolaDB.getGroupById(group.id)
            assert(g != None)
            assert(g.get.name == group.name)

            val a = PayolaDB.getAnalysisById(analysis.id)
            assert(a != None)
            assert(a.get.name == analysis.name)

            val p = PayolaDB.getPluginById(plugin.id)
            assert(p != None)
            assert(p.get.name == plugin.name)

            val pi = PayolaDB.getPluginInstanceById(pluginInstance.id)
            assert(pi != None)
            */
        }
    }
}
