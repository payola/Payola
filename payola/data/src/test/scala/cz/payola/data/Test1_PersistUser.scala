package cz.payola.data

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.data.dao.UserDAO

class Test1_PersistUser extends FlatSpec with ShouldMatchers
{
    val userDao = new UserDAO()

    val u1 = new cz.payola.domain.entities.User("name")

    "Users" should "be persited, loaded and managed by UserDAO" in {

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
}
