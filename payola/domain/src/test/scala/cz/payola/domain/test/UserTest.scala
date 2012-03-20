package cz.payola.domain.test

import cz.payola.domain._
import entities.{Group, User}
import org.scalatest.FlatSpec
import org.scalatest.matchers._

class UserTest extends FlatSpec with ShouldMatchers {
    "User" should "retain values passed in the constructor" in {
        new User("Franta").name == "Franta"
    }

    "User" should "not be initialized with null or empty string" in {
        evaluating(new User(null)) should produce [IllegalArgumentException]
        evaluating(new User("")) should produce [IllegalArgumentException]
    }

    "User" should "not add null Group" in  {
        val u: User = new User("Franta")
        evaluating(u.addToGroup(null)) should produce [IllegalArgumentException]
    }

    "User" should "not be removed from null Group" in  {
        val u: User = new User("Franta")
        evaluating(u.removeFromGroup(null)) should produce [IllegalArgumentException]
    }

    "User" should "not be a member and should be an owner of group when added" in  {
        val u: User = new User("Franta")
        val g: Group = new Group("Grupa", u);
        !u.isMemberOfGroup(g) && u.isOwnerOfGroup(g)
    }

    "User" should "be a member but not an owner of group when added" in  {
        val u1: User = new User("Franta")
        val u2: User = new User("Pepa")
        val g: Group = new Group("Grupa", u1);
        u2.addToGroup(g)
        u2.isMemberOfGroup(g) && !u2.isOwnerOfGroup(g)
    }

    "User" should "update its group ownerships" in  {
        val u: User = new User("Franta")
        u.ownedGroups.size should be (0)

        val u2: User = new User("Pepa")
        val g: Group = new Group("Monoid", u);
        u.ownedGroups.size should be (1)

        u2.memberGroups.size should be (0)

        u2.addToGroup(g)
        u2.memberGroups.size should be (1)

        u2.removeFromGroup(g)
        u2.memberGroups.size should be (0)

        !u.isMemberOfGroup(g)
    }

    "User" should "not be removed from the group while still being an " +
        "owner of the group" in  {
        val u: User = new User("Franta")
        val g: Group = new Group("Monoid", u)
        evaluating(u.removeOwnedGroup(g)) should produce [IllegalArgumentException]
    }

    "User" should "not be renamed to null or empty string" in  {
        val u: User = new User("Franta")
        evaluating(u.setName(null)) should produce [IllegalArgumentException]
        evaluating(u.setName("")) should produce [IllegalArgumentException]
    }
}
