package cz.payola.data.entities

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class SquerylSpecs extends FlatSpec with ShouldMatchers
{

    "Database" should "be created succesfuly" in {
        // Init
        PayolaDB.startDatabaseSession()
        PayolaDB.createSchema()
    }

    "1) User" should "be persited" in {
        val user = new User("1", "name1", "pwd1", "email1")
        PayolaDB.save(user);

        val u = PayolaDB.getById(user.id)
        assert (u != None)
        assert (u.get.id == user.id)
        assert (u.get.name == user.name)
        assert (u.get.password == user.password)
        assert (u.get.email == user.email)

        val u2 = PayolaDB.getById("")
        assert (u2 == None)
    }

    /*
    "2) Group" should "be persisted" in {
        val owner = PayolaDB.getById("1").get

        val group = new Group("1", "group1", owner)

        //PayolaDB.save(group)
    }
    */
}
