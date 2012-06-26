package cz.payola.data

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class Test0_DatabaseInitialization extends FlatSpec with ShouldMatchers
{
    "Connection to DB" should "be initialized" in {
        PayolaDB.connect(true)
    }

    "Schema" should "be created" in {
        PayolaDB.createSchema()
    }
}
