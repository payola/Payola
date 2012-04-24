package cz.payola.domain.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.domain.entities.parameters.{ParameterInstance, BooleanParameter}

class BooleanParameterTest extends FlatSpec with ShouldMatchers {
/*    "BooleanParameter" should "not get initialized with null or an empty string" in {
        evaluating(new BooleanParameter(n = null, defaultValue = true)) should produce [IllegalArgumentException]
        evaluating(new BooleanParameter(n = "", defaultValue = true)) should produce [IllegalArgumentException]
    }
    */

    "BooleanParameter" should "not allow setting empty or null name" in {
        val bp: BooleanParameter = new BooleanParameter(n = "Test", defaultValue = true)
        evaluating(bp.name_=("")) should produce [IllegalArgumentException]
        evaluating(bp.name_=(null)) should produce [IllegalArgumentException]

        evaluating(bp.name = "") should produce [IllegalArgumentException]
        evaluating(bp.name = null) should produce [IllegalArgumentException]
    }
    
    "BooleanParameter" should "return instance with default value when passing no params" in {
        val bp: BooleanParameter = new BooleanParameter(n = "Test", defaultValue = true)
        val bpVal: ParameterInstance[Boolean] = bp.createInstance()
        bpVal.booleanValue should equal (true)

        val bp2: BooleanParameter = new BooleanParameter(n = "Test", defaultValue = false)
        val bpVal2: ParameterInstance[Boolean] = bp2.createInstance()
        bpVal2.booleanValue should equal (false)
    }

    "BooleanParameterValue" should "return valid values" in {
        val bp: BooleanParameter = new BooleanParameter(n = "Test", defaultValue = true)
        val bpVal: ParameterInstance[Boolean] = bp.createInstance()
        
        bpVal.booleanValue should equal (true)
        bpVal.intValue should equal  (1)
        bpVal.floatValue should equal (1.0f)
        bpVal.stringValue should equal ("true")
        
        bpVal.setBooleanValue(false)
        bpVal.booleanValue should equal (false)
        bpVal.intValue should equal  (0)
        bpVal.floatValue should equal (0.0f)
        bpVal.stringValue should equal ("false")
    }

    "BooleanParameterValue" should "allow setting valid values and" +
        " handle the invalid ones gracefully" in {

        val bp: BooleanParameter = new BooleanParameter(n = "Test", defaultValue = true)
        val bpVal: ParameterInstance[Boolean] = bp.createInstance()

        bpVal.setIntValue(0)
        bpVal.booleanValue should equal (false)
        bpVal.setIntValue(4)
        bpVal.booleanValue should equal (true)

        bpVal.setFloatValue(0.0f)
        bpVal.booleanValue should equal (false)
        bpVal.setFloatValue(4.0f)
        bpVal.booleanValue should equal (true)

        bpVal.setStringValue("true")
        bpVal.booleanValue should equal (true)
        bpVal.setStringValue("YES")
        bpVal.booleanValue should equal (true)
        bpVal.setStringValue("yes")
        bpVal.booleanValue should equal (true)
        bpVal.setStringValue("y")
        bpVal.booleanValue should equal (true)

        bpVal.setStringValue("false")
        bpVal.booleanValue should equal (false)
        bpVal.setStringValue("NO")
        bpVal.booleanValue should equal (false)
        bpVal.setStringValue("no")
        bpVal.booleanValue should equal (false)
        bpVal.setStringValue("n")
        bpVal.booleanValue should equal (false)
    }



}

