package cz.payola.model.test

import cz.payola.model._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

/**
 * User: Krystof Vasa
 * Date: 23.11.11
 * Time: 19:23
 */

class GroupTest extends FlatSpec with ShouldMatchers {
    "Group" should "throw exception being null-initiated" in {
        evaluating(new Group(null, null)) should produce [AssertionError]
    }

    "Group" should "throw exception being empty-string-initiated" in {
        evaluating(new Group("", null)) should produce [AssertionError]
    }

    "Group" should "throw exception being initiated with null user" in {
        evaluating(new Group("Monoid", null)) should produce [AssertionError]
    }

    "Group" should "be owned by the user that it's being passed in the constructor" in {
        val u: User = new User("Franta")
        val g: Group = new Group("Grupa", u)
        g.isOwnedByUser(u)
    }

    "Group" should "have its owner as a member" in {
        val u: User = new User("Franta")
        val g: Group = new Group("Grupa", u)
        g.hasMember(u)
    }

    "Group" should "not add a null user" in  {
        val u: User = new User("Franta")
        val g: Group = new Group("Grupa", u)
        evaluating(g.addMember(null)) should produce [AssertionError]
    }

    "Group" should "not remove a null user" in  {
        val u: User = new User("Franta")
        val g: Group = new Group("Grupa", u)
        evaluating(g.removeMember(null)) should produce [AssertionError]
    }

    "Group" should "contain the user after being added" in {
        val u: User = new User("Franta")
        val u2: User = new User("Pepa")
        val g: Group = new Group("Monoid", u)
        g.addMember(u2)
        g.hasMember(u2)
    }

    "Group" should "not contain the user after he's been removed" in {
        val u: User = new User("Franta")
        val u2: User = new User("Pepa")
        val g: Group = new Group("Monoid", u)
        g.addMember(u2)
        g.removeMember(u2)
        !g.hasMember(u2)
    }

    "Group" should "not allow removing the owner" in {
        val u: User = new User("Franta")
        val g: Group = new Group("Monoid", u)

        evaluating(g.removeMember(u)) should  produce [AssertionError]
    }


}

