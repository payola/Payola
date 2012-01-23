package cz.payola.model.test

import cz.payola.model.parameter._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class IntParameterTest extends FlatSpec with ShouldMatchers {
    "IntParameter" should "not get initialized with null or an empty string" in {
        evaluating(new IntParameter(null, 1)) should produce [IllegalArgumentException]
        evaluating(new IntParameter("", 1)) should produce [IllegalArgumentException]
    }

    "IntParameter" should "not allow setting empty or null name" in {
        val ip: IntParameter = new IntParameter("Test", 1)
        evaluating(ip.setName("")) should produce [IllegalArgumentException]
        evaluating(ip.setName(null)) should produce [IllegalArgumentException]

        evaluating(ip.name = "") should produce [IllegalArgumentException]
        evaluating(ip.name = null) should produce [IllegalArgumentException]
    }

    "IntParameter" should "return instance with default value when passing no params" in {
        val ip: IntParameter = new IntParameter("Test", 1)
        val ipVal: ParameterInstance[Int] = ip.createInstance()
        ipVal.intValue should equal (1)

        val ip2: IntParameter = new IntParameter("Test", 5)
        val ipVal2: ParameterInstance[Int] = ip2.createInstance()
        ipVal2.intValue should equal (5)
    }

    "IntParameterValue" should "return valid values" in {
        val ip: IntParameter = new IntParameter("Test", 1)
        val ipVal: ParameterInstance[Int] = ip.createInstance()

        ipVal.booleanValue should equal (true)
        ipVal.intValue should equal  (1)
        ipVal.floatValue should equal (1.0f)
        ipVal.stringValue should equal ("1")

        ipVal.setIntValue(0)
        ipVal.booleanValue should equal (false)
        ipVal.intValue should equal  (0)
        ipVal.floatValue should equal (0.0f)
        ipVal.stringValue should equal ("0")
    }

    "IntParameterValue" should "allow setting valid values and" +
        " handle the invalid ones gracefully" in {

        val ip: IntParameter = new IntParameter("Test", 1)
        val ipVal: ParameterInstance[Int] = ip.createInstance()

        ipVal.setBooleanValue(false)
        ipVal.intValue should equal (0)
        ipVal.setBooleanValue(true)
        ipVal.intValue should equal (1)

        ipVal.setFloatValue(0.0f)
        ipVal.intValue should equal (0)
        ipVal.setFloatValue(4.0f)
        ipVal.intValue should equal (4)

        ipVal.setStringValue("true")
        ipVal.intValue should equal (0)
        ipVal.setStringValue("4")
        ipVal.intValue should equal  (4)
    }



}

