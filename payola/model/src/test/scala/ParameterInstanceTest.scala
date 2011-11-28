package cz.payola.model.test

import cz.payola.model._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class ParameterInstanceTest extends FlatSpec with ShouldMatchers {
    "ParameterInstance" should "not allow initialization with null values" in {
        evaluating(new ParameterInstance(null, null)) should produce [AssertionError]
    }

    "ParameterInstance" should "retain values passed in the constructor" in {
        val p: Parameter = new Parameter("param")
        p.addConstrain(ParameterConstrains.ParameterConstrainString)
        val instance: ParameterInstance = new ParameterInstance(p, "test")
        assume(instance.parameter == p && instance.stringValue == "test")
    }

    "Parameter" should "allow int getters and setters only when allowed by constrains" in {
        val p: Parameter = new Parameter("param")
        p.addConstrain(ParameterConstrains.ParameterConstrainString)

        val instance: ParameterInstance = new ParameterInstance(p, "test")
        evaluating(instance.intValue) should produce [AssertionError]
        evaluating(instance.setIntValue(3)) should produce [AssertionError]

        // Now add the int constrain
        p.addConstrain(ParameterConstrains.ParameterConstrainInt)
        instance.setIntValue(3)
        assume(instance.intValue == 3)
        assume(instance.stringValue == "3")
    }


}

