package cz.payola.domain.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.domain._
import entities.{Analysis, User}

class AnalysisShareTest extends FlatSpec with ShouldMatchers {
    "AnalysisShare" should "not be initialized with null analysis or privileges" in {
        val u: User = new User("Franta")
        val a: Analysis = new Analysis("HelloWorld", u)
        evaluating(new AnalysisShare(null, null)) should produce [IllegalArgumentException]
        evaluating(new AnalysisShare(a, null)) should produce [IllegalArgumentException]
        evaluating(new AnalysisShare(null, SharePrivilege.SharePrivilegeResultOnly)) should produce [IllegalArgumentException]

        // This one is a valid call
        new AnalysisShare(a, SharePrivilege.SharePrivilegeIncludingData)
    }

}
