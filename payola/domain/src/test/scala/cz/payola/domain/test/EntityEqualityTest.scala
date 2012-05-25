package cz.payola.domain.test

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import cz.payola.domain.entities.{Analysis, User}

class UserWithConcreteId(name: String, override val id: String)
    extends User(name)

class AnalysisWithConcreteId(name: String, owner: Option[User], override val id: String)
    extends Analysis(name, owner)

class EntityEqualityTest extends FlatSpec with ShouldMatchers {
    "Entities" should "not be equal if classes are not the same, yet IDs are" in {
        val u = new UserWithConcreteId("Franta", "3")
        val a = new AnalysisWithConcreteId("My Analysis", Some(u), "3")
        (u == a) should equal (false)
    }

    it should "be equal if both classes and IDs are the same, yet other content isn't" in {
        val u1 = new UserWithConcreteId("Franta", "3")
        val u2 = new UserWithConcreteId("Ben", "3")
        (u1 == u2) should equal (true)
    }
}
