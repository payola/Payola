package cz.payola.model.test

import cz.payola.model.parameter._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class FloatParameterTest extends FlatSpec with ShouldMatchers {
    "FloatParameter" should "not get initialized with null or an empty string" in {
        evaluating(new FloatParameter(null, 1.0f)) should produce [AssertionError]
        evaluating(new FloatParameter("", 1.0f)) should produce [AssertionError]
    }

    "FloatParameter" should "not allow setting empty or null name" in {
        val fp: FloatParameter = new FloatParameter("Test", 1.0f)
        evaluating(fp.setName("")) should produce [AssertionError]
        evaluating(fp.setName(null)) should produce [AssertionError]

        evaluating(fp.name = "") should produce [AssertionError]
        evaluating(fp.name = null) should produce [AssertionError]
    }

    "FloatParameter" should "return instance with default value when passing no params" in {
        val fp: FloatParameter = new FloatParameter("Test", 1.0f)
        val fpVal: ParameterInstance[Float] = fp.createInstance()
        fpVal.floatValue should equal (1.0f)

        val fp2: FloatParameter = new FloatParameter("Test", 5.0f)
        val fpVal2: ParameterInstance[Float] = fp2.createInstance()
        fpVal2.intValue should equal (5.0f)
    }

    "FloatParameterValue" should "return valid values" in {
        val fp: FloatParameter = new FloatParameter("Test", 1.0f)
        val fpVal: ParameterInstance[Float] = fp.createInstance()

        fpVal.booleanValue should equal (true)
        fpVal.intValue should equal  (1)
        fpVal.floatValue should equal (1.0f)
        fpVal.stringValue should equal ("1.0")

        fpVal.setFloatValue(0.0f)
        fpVal.booleanValue should equal (false)
        fpVal.intValue should equal  (0)
        fpVal.floatValue should equal (0.0f)
        fpVal.stringValue should equal ("0.0")
    }

    "FloatParameterValue" should "allow setting valid values and" +
        " handle the invalid ones gracefully" in {

        val fp: FloatParameter = new FloatParameter("Test", 1.0f)
        val fpVal: ParameterInstance[Float] = fp.createInstance()

        fpVal.setBooleanValue(false)
        fpVal.floatValue should equal (0.0f)
        fpVal.setBooleanValue(true)
        fpVal.floatValue should equal (1.0f)

        fpVal.setIntValue(0)
        fpVal.floatValue should equal (0.0f)
        fpVal.setIntValue(4)
        fpVal.floatValue should equal (4.0f)

        fpVal.setStringValue("true")
        fpVal.floatValue should equal (0.0f)
        fpVal.setStringValue("4.0")
        fpVal.floatValue should equal (4.0f)
    }



}

