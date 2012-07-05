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

    "Plugins" should "be persisted with their parameters by PluginDAO" in {
        TestObject.persistPlugins
    }

    "Analysis" should "be stored/updated/loaded by AnalysisDAO" in {
        TestObject.persistAnalyses
    }

    "DataSources" should "be updated/stored by DataSourceDAO" in {
        TestObject.persistDataSources
    }

    "Privileges" should "be granted and persisted properly" in {
        TestObject.persistPrivileges
    }

    "Pagionation" should "work" in {
        TestObject.testPagination
    }

    "Entities" should "be with removed their related entities" in {
        TestObject.testCascadeDeletes

    }
}
