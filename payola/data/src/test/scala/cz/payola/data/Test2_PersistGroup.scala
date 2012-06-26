package cz.payola.data

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.data.dao._
import org.scalatest.Assertions._

class Test2_PersistGroup  extends FlatSpec with ShouldMatchers
{
    val groupDao = new GroupDAO()

    val user = new UserDAO().findByUsername("n", 0, 1)(0)

    val g1 = new cz.payola.domain.entities.Group("group1", user)
    val g2 = new cz.payola.domain.entities.Group("group2", user)

    "Groups" should "be persisted, loaded and managed by GroupDAO" in {

        val group1 = groupDao.persist(g1).get
        val group2 = groupDao.persist(g2).get

        var g = groupDao.getById(group1.id)
        assert(g != None)
        assert(g.get.id == group1.id)
        assert(g.get.name == group1.name)
        assert(g.get.owner.id == user.id)

        g = groupDao.getById(group2.id)
        assert(g != None)
        assert(g.get.id == group2.id)
        assert(g.get.name == group2.name)
        assert(g.get.owner.id == user.id)

        //assert(user.ownedGroups.size == 2)
    }

    "Group" should "maintain members collection" in {
        val group = groupDao.getById(g1.id)
    }
}
