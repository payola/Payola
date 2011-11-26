package cz.payola.model.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.model._

class AnalysisShareTest extends FlatSpec with ShouldMatchers {
    "AnalysisShare" should "not be initialized with null analysis or privileges" in {
        val u: User = new User("Franta")
        val a: Analysis = new Analysis("HelloWorld", u)
        evaluating(new AnalysisShare(null, null)) should produce [AssertionError]
        evaluating(new AnalysisShare(a, null)) should produce [AssertionError]
        evaluating(new AnalysisShare(null, SharePrivilege.SharePrivilegeResultOnly)) should produce [AssertionError]

        // This one is a valid call
        new AnalysisShare(a, SharePrivilege.SharePrivilegeIncludingData)
    }

}
