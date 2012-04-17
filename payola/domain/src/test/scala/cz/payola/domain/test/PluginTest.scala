package cz.payola.domain.test

import cz.payola.domain._
import cz.payola.domain.parameter._
import entities.Plugin
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import collection.mutable.ArrayBuffer

class PluginTest extends FlatSpec with ShouldMatchers {
    "Plugin" should "not get initialized with null or an empty string or a null array" in {
        evaluating(new Plugin(null)) should produce [IllegalArgumentException]
        evaluating(new Plugin("")) should produce [IllegalArgumentException]
        evaluating(new Plugin("Something", null)) should produce [IllegalArgumentException]

        // Shouldn't produce an exception
        new Plugin("Something", new ArrayBuffer[Parameter[_]])
    }

    "Plugin" should "have sane getters and setters" in {
        val p: Plugin = new Plugin("MyPlugin", new ArrayBuffer[Parameter[_]]())
        val param: StringParameter = new StringParameter("Hello", "")

        p.containsParameter(param) should be (false)

        evaluating(p.addParameter(null)) should produce[IllegalArgumentException]
        p.addParameter(param)
        p.containsParameter(param) should be (true)

        p.removeParameter(param)
        // second removal should produce an exception
        evaluating(p.removeParameter(param)) should produce[IllegalArgumentException]

        evaluating(p.setName(null)) should produce[IllegalArgumentException]
        evaluating(p.setName("")) should produce[IllegalArgumentException]

        p.setName("NewName")
        p.name should be ("NewName")
    }

}

