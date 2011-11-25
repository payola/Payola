package cz.payola.model.test

import cz.payola.model._
import org.scalatest.FlatSpec
import org.scalatest.matchers._

/**
 * User: Krystof Vasa
 * Date: 23.11.11
 * Time: 16:19
 */

class UserTest extends FlatSpec with ShouldMatchers {
    "User" should "have name Franta" in {
        new User("Franta").name == "Franta"
    }

    "User" should "throw exception being null-initiated" in {
        evaluating(new User(null)) should produce [AssertionError]
    }

    "User" should "throw exception being empty-string-initiated" in {
        evaluating(new User("")) should produce [AssertionError]
    }

    "User" should "not add null Group" in  {
        val u: User = new User("Franta")
        evaluating(u.addToGroup(null)) should produce [AssertionError]
    }

    "User" should "not be removed from null Group" in  {
        val u: User = new User("Franta")
        evaluating(u.removeFromGroup(null)) should produce [AssertionError]
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

    "User" should "not be a member of group after removing" in  {
        val u: User = new User("Franta")
        val u2: User = new User("Pepa")
        val g: Group = new Group("Monoid", u);
        u2.addToGroup(g)
        u2.removeFromGroup(g)
        !u.isMemberOfGroup(g)
    }

    "User" should "be an owner of 0 groups" in  {
        val u: User = new User("Franta")
        u.ownedGroups.size == 0
    }

    "User" should "be an owner of 1 group" in  {
        val u: User = new User("Franta")
        val g: Group = new Group("Monoid", u)
        u.ownedGroups.size == 1
    }

    "User" should "not be removed from the group while still being an " +
        "owner of the group" in  {
        val u: User = new User("Franta")
        val g: Group = new Group("Monoid", u)
        evaluating(u.removeOwnedGroup(g)) should produce [AssertionError]
    }

    "User" should "not be renamed to null or empty string" in  {
        val u: User = new User("Franta")
        evaluating(u.setName(null)) should produce [AssertionError]
        evaluating(u.setName("")) should produce [AssertionError]
    }
}
