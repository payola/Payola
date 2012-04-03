package cz.payola.data.entities

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class SquerylSpecs extends FlatSpec with ShouldMatchers
{
    // Init
    PayolaDB.startDatabaseSession()

    "Database" should "be created succesfuly" in {
        PayolaDB.createSchema()
    }

    "1) User" should "be persited" in {
        val user = new User("1", "name1", "pwd1", "email1")
        PayolaDB.save(user);

        val u = PayolaDB.getUserById(user.id)
        assert (u != None)
        assert (u.get.id == user.id)
        assert (u.get.name == user.name)
//        assert (u.get.password == user.password)
        assert (u.get.email == user.email)

        val u2 = PayolaDB.getUserById("")
        assert (u2 == None)
    }

    ///*
    "2) Group" should "be persisted" in {
        val user = PayolaDB.getUserById("1").get

        val group = new Group("1", "group1", user)

        PayolaDB.save(group)

        println(user.ownedGroups2)
    }
    //*/


}
