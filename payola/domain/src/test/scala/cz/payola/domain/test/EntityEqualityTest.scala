package cz.payola.domain.test

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import cz.payola.domain.entities.{Analysis, User}

class EntityEqualityTest extends FlatSpec with ShouldMatchers {
    "Entities" should "not be equal if classes are not the same, yet IDs are" in {
        val u: User = new User("3", "Franta")
        val a: Analysis = new Analysis("3", "My Analysis", u)

        (u == a) should equal (false)
    }

    "Entities" should "be equal if both classes and IDs are the same, yet other content isn't" in {
        val u: User = new User("3", "Franta")
        val u2: User = new User("3", "Beda")

        (u == u2) should equal (true)
    }


}
