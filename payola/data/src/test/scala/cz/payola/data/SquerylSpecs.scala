package cz.payola.data

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class SquerylSpecs extends FlatSpec with ShouldMatchers
{
    "Connection to DB" should "be initialized" in {
        TestObject.connect
    }

    "Schema" should "be created" in {
        TestObject.createSchema
    }

    "Users" should "be persited, loaded and managed by UserDAO" in {
        TestObject.persistUsers
    }

    "Groups" should "be persisted, loaded and managed by GroupDAO" in {
        TestObject.persistGroups
    }

    "Group" should "maintain members collection" in {
        TestObject.testGroupMembership
    }

    "Analysis" should "be stored/updated/loaded by AnalysisDAO" in {
        TestObject.persistAnalyses
    }

    "Pagionation" should "work" in {
        TestObject.testPagination
    }

    "Entities" should "be with removed their related entities" in {
        TestObject.testCascadeDeletes

    }
}
