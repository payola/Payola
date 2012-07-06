package cz.payola.data

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class SquerylSpecs extends FlatSpec with ShouldMatchers
{
    "Connection to DB" should "be initialized" in {
        val dataContextComponent = new SquerylDataContextComponent
        {
            val schema = new SquerylSchema
        }
        println("Connecting")
        dataContextComponent.schema.connect(true)
        println("Creating")
        dataContextComponent.schema.createSchema
        println("Persisting user")
        dataContextComponent.userDAO.persist(new cz.payola.domain.entities.User("HS"))
        println("Retrieving users")
        val users = dataContextComponent.userDAO.getAll()
        println(users)
        println(users.head.context)
    }

    /*"Schema" should "be created" in {
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

    }*/
}
