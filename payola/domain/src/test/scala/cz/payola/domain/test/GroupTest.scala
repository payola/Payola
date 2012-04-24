package cz.payola.domain.test

import cz.payola.domain._
import entities.{Group, User}
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class GroupTest extends FlatSpec with ShouldMatchers {
    "Group" should "not be initialized with null or empty values" in {
//        evaluating(new Group(_name = "", _owner = null)) should produce [IllegalArgumentException]
        evaluating(new Group(_name = "Monoid", _owner = null)) should produce [IllegalArgumentException]
    }

    "Group" should "retain values passed in the constructor" in {
        val u: User = new User(_name = "Franta")
        val g: Group = new Group(_name = "Grupa", _owner = u)
        g.name should be ("Grupa")
        g.owner should be (u)
    }

    "Group" should "have its owner as a member" in {
        val u: User = new User(_name = "Franta")
        val g: Group = new Group(_name = "Grupa", _owner = u)
        g.isOwnedByUser(u) && !g.hasMember(u)
    }

    "Group" should "not add nor remove a null user" in  {
        val u: User = new User(_name = "Franta")
        val g: Group = new Group(_name = "Grupa", _owner = u)
        evaluating(g.addMember(null)) should produce [IllegalArgumentException]
        evaluating(g.removeMember(null)) should produce [IllegalArgumentException]
    }

    "Group" should "contain the user after being added" in {
        val u: User = new User(_name = "Franta")
        val u2: User = new User(_name = "Pepa")
        val g: Group = new Group(_name = "Monoid", _owner = u)
        g.addMember(u2)
        g.hasMember(u2)
    }

    "Group" should "not contain the user after he's been removed" in {
        val u: User = new User(_name = "Franta")
        val u2: User = new User(_name = "Pepa")
        val g: Group = new Group(_name = "Monoid", _owner = u)
        g.addMember(u2)
        g.removeMember(u2)
        !g.hasMember(u2)
    }

    "User" should "not be renamed to null or empty string" in  {
        val u: User = new User(_name = "Franta")
        val g: Group = new Group(_name = "Pologrupa", _owner = u)
        evaluating(g.name_=(null)) should produce [IllegalArgumentException]
        evaluating(g.name_=("")) should produce [IllegalArgumentException]
    }

}

