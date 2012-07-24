package cz.payola.domain.test

import cz.payola.domain._
import entities.{Group, User}
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.common.ValidationException

class GroupTest extends FlatSpec with ShouldMatchers {
    it should "retain values passed in the constructor" in {
        val u: User = new User(_name = "Franta")
        val g: Group = new Group(_name = "Grupa", _owner = u)
        g.name should be ("Grupa")
        g.owner should be (u)
    }

    it should "not have its owner as a member" in {
        val u: User = new User(_name = "Franta")
        val g: Group = new Group(_name = "Grupa", _owner = u)
        g.owner == u && !g.hasMember(u)
    }

    it should "not add nor remove a null user" in  {
        val u: User = new User(_name = "Franta")
        val g: Group = new Group(_name = "Grupa", _owner = u)
        evaluating(g.addMember(null)) should produce [IllegalArgumentException]
        evaluating(g.removeMember(null)) should produce [IllegalArgumentException]
    }

    it should "contain the user after being added" in {
        val u: User = new User(_name = "Franta")
        val u2: User = new User(_name = "Pepa")
        val g: Group = new Group(_name = "Monoid", _owner = u)
        g.addMember(u2)
        g.hasMember(u2)
    }

    it should "not contain the user after he's been removed" in {
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
        evaluating(g.name_=(null)) should produce [ValidationException]
        evaluating(g.name_=("")) should produce [ValidationException]
    }

}
