package cz.payola.domain.test

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import cz.payola.domain.entities.{Analysis, User}

trait OverriddenID{
    val id: String
}

class UserWithTheSameID(n: String) extends User(n) {
    override val id = "3"
}

class EntityEqualityTest extends FlatSpec with ShouldMatchers {
    "Entities" should "not be equal if classes are not the same, yet IDs are" in {
        val u: User = new User("Franta") with OverriddenID { override val id = "3" }
        val a: Analysis = new Analysis("My Analysis", Some(u)) with OverriddenID { override val id = "3" }

        (u == a) should equal (false)
    }

    it should "be equal if both classes and IDs are the same, yet other content isn't" in {
        val u: User = new UserWithTheSameID("Franta")
        val u2: User = new UserWithTheSameID("Ben")
        (u == u2) should equal (true)
    }


}
