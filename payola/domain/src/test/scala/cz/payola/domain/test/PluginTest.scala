package cz.payola.domain.test

import cz.payola.domain._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import collection.mutable.ArrayBuffer

/*
class PluginTest extends FlatSpec with ShouldMatchers {
    "Plugin" should "not get initialized with null or an empty string or a null array" in {
//        evaluating(new Plugin(_name = null)) should produce [IllegalArgumentException]
//        evaluating(new Plugin(_name = "")) should produce [IllegalArgumentException]

        // Shouldn't produce an exception
        new Plugin(_name = "Something")
    }

    "Plugin" should "have sane getters and setters" in {
        val p: Plugin = new Plugin(_name = "MyPlugin")
        val param: StringParameter = new StringParameter(n = "Hello", defaultValue = "")

        p.containsParameter(param) should be (false)

        evaluating(p.addParameter(null)) should produce[IllegalArgumentException]
        p.addParameter(param)
        p.containsParameter(param) should be (true)

        p.removeParameter(param)
        // second removal should produce an exception
        evaluating(p.removeParameter(param)) should produce[IllegalArgumentException]

        evaluating(p.name_=(null)) should produce[IllegalArgumentException]
        evaluating(p.name_=("")) should produce[IllegalArgumentException]

        p.name_=("NewName")
        p.name should be ("NewName")
    }

}
*/
