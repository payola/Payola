package cz.payola.model.test

import cz.payola.model._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class ParameterTest extends FlatSpec with ShouldMatchers {
    "Parameter" should "not allow null or empty name" in {
        evaluating(new Parameter(null)) should produce [AssertionError]
        evaluating(new Parameter("")) should produce [AssertionError]

        val p: Parameter = new Parameter("param")
        evaluating(p.name = null) should produce [AssertionError]
        evaluating(p.name = "") should produce [AssertionError]
    }

    "Parameter" should "retain values passed in the constructor" in {
        val p: Parameter = new Parameter("param")
        p.name == "param"
    }

    "Parameter" should "not allow invalid constrains" in {
        val p: Parameter = new Parameter("param")
        val invalidConstrain: Int = 1 << 30
        evaluating(p.addConstrain(invalidConstrain)) should produce [AssertionError]
        evaluating(p.removeConstrain(invalidConstrain)) should produce [AssertionError]
        evaluating(p.setValueConstrains(invalidConstrain)) should produce [AssertionError]
        evaluating(p.hasValueConstrain(invalidConstrain)) should produce [AssertionError]
    }

    "Parameter" should "allow valid constrains" in {
        val p: Parameter = new Parameter("param")
        val validConstrain: Int = ParameterConstrains.ParameterConstrainInt
        val validConstrain2: Int = ParameterConstrains.ParameterConstrainFloat
        val validConstrains: Int = validConstrain | validConstrain2

        p.addConstrain(validConstrain)
        assume(p.hasValueConstrain(validConstrain))
        p.removeConstrain(validConstrain)
        assume(!p.hasValueConstrain(validConstrain))

        p.setValueConstrains(validConstrains)
        assume(p.hasValueConstrain(validConstrain) && p.hasValueConstrain(validConstrain2))
    }

}

