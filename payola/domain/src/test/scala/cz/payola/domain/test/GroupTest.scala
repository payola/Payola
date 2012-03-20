package cz.payola.domain.test

import cz.payola.domain._
import entities.{Group, User}
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class GroupTest extends FlatSpec with ShouldMatchers {
    "Group" should "not be initialized with null or empty values" in {
        evaluating(new Group(null, null)) should produce [IllegalArgumentException]
        evaluating(new Group("", null)) should produce [IllegalArgumentException]
        evaluating(new Group("Monoid", null)) should produce [IllegalArgumentException]
    }

    "Group" should "retain values passed in the constructor" in {
        val u: User = new User("Franta")
        val g: Group = new Group("Grupa", u)
        g.name should be ("Grupa")
        g.owner should be (u)
    }

    "Group" should "have its owner as a member" in {
        val u: User = new User("Franta")
        val g: Group = new Group("Grupa", u)
        g.isOwnedByUser(u) && !g.hasMember(u)
    }

    "Group" should "not add nor remove a null user" in  {
        val u: User = new User("Franta")
        val g: Group = new Group("Grupa", u)
        evaluating(g.addMember(null)) should produce [IllegalArgumentException]
        evaluating(g.removeMember(null)) should produce [IllegalArgumentException]
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

    "User" should "not be renamed to null or empty string" in  {
        val u: User = new User("Franta")
        val g: Group = new Group("Pologrupa", u)
        evaluating(g.setName(null)) should produce [IllegalArgumentException]
        evaluating(g.setName("")) should produce [IllegalArgumentException]
    }

}

