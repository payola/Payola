package cz.payola.domain.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.domain.entities.parameters.{ParameterInstance, StringParameter}
import org.omg.CosNaming._NamingContextExtStub

class StringParameterTest extends FlatSpec with ShouldMatchers {
/*    "StringParameter" should "not get initialized with null or an empty string" in {
        evaluating(new StringParameter(n = null, defaultValue = "Help")) should produce [IllegalArgumentException]
        evaluating(new StringParameter(n = "", defaultValue = "Help")) should produce [IllegalArgumentException]
    }
*/

    "StringParameter" should "not allow setting empty or null name" in {
        val sp: StringParameter = new StringParameter(n = "Test", defaultValue = "Help")
        evaluating(sp.name_=("")) should produce [IllegalArgumentException]
        evaluating(sp.name_=(null)) should produce [IllegalArgumentException]

        evaluating(sp.name = "") should produce [IllegalArgumentException]
        evaluating(sp.name = null) should produce [IllegalArgumentException]
    }

    "StringParameter" should "return instance with default value when passing no params" in {
        val sp: StringParameter = new StringParameter(n = "Test", defaultValue = "Help")
        val spVal: ParameterInstance[String] = sp.createInstance()
        spVal.stringValue should equal ("Help")
    }

    "StringParameterValue" should "return valid values" in {
        val sp: StringParameter = new StringParameter(n = "Test", defaultValue = "true")
        val spVal: ParameterInstance[String] = sp.createInstance()

        spVal.booleanValue should equal (true)
        spVal.intValue should equal  (0)
        spVal.floatValue should equal (0.0f)
        spVal.stringValue should equal ("true")

        spVal.setStringValue("44")
        spVal.booleanValue should equal (false)
        spVal.intValue should equal  (44)
        spVal.floatValue should equal (44.0f)
        spVal.stringValue should equal ("44")
    }

    "StringParameterValue" should ("allow setting valid values and" +
        " handle the invalid ones gracefully") in {

        val sp: StringParameter = new StringParameter(n = "Test", defaultValue = "")
        val spVal: ParameterInstance[String] = sp.createInstance()

        spVal.setBooleanValue(false)
        spVal.stringValue should equal ("false")
        spVal.setBooleanValue(true)
        spVal.stringValue should equal ("true")

        spVal.setIntValue(0)
        spVal.stringValue should equal ("0")
        spVal.setIntValue(4)
        spVal.stringValue should equal ("4")

        spVal.setFloatValue(0.0f)
        spVal.stringValue should equal ("0.0")
        spVal.setFloatValue(4.0f)
        spVal.stringValue should equal ("4.0")
    }



}

