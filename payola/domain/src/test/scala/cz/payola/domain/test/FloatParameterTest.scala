package cz.payola.domain.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.domain.entities.parameters._

class FloatParameterTest extends FlatSpec with ShouldMatchers {
/*    "FloatParameter" should "not get initialized with null or an empty string" in {
        evaluating(new FloatParameter(n = null, defaultValue = 1.0f)) should produce [IllegalArgumentException]
        evaluating(new FloatParameter(n = "", defaultValue = 1.0f)) should produce [IllegalArgumentException]
    }
    */

    "FloatParameter" should "not allow setting empty or null name" in {
        val fp: FloatParameter = new FloatParameter(n = "Test", defaultValue = 1.0f)
        evaluating(fp.name_=("")) should produce [IllegalArgumentException]
        evaluating(fp.name_=(null)) should produce [IllegalArgumentException]

        evaluating(fp.name = "") should produce [IllegalArgumentException]
        evaluating(fp.name = null) should produce [IllegalArgumentException]
    }

    "FloatParameter" should "return instance with default value when passing no params" in {
        val fp: FloatParameter = new FloatParameter(n = "Test", defaultValue = 1.0f)
        val fpVal: ParameterInstance[Float] = fp.createInstance()
        fpVal.floatValue should equal (1.0f)

        val fp2: FloatParameter = new FloatParameter(n = "Test", defaultValue = 5.0f)
        val fpVal2: ParameterInstance[Float] = fp2.createInstance()
        fpVal2.intValue should equal (5.0f)
    }

    "FloatParameterValue" should "return valid values" in {
        val fp: FloatParameter = new FloatParameter(n = "Test", defaultValue = 1.0f)
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

        val fp: FloatParameter = new FloatParameter(n = "Test", defaultValue = 1.0f)
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

