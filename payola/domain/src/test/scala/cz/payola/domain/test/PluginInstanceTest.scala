package cz.payola.domain.test

import cz.payola.domain._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import collection.mutable.ArrayBuffer
/*
class PluginInstanceTest extends FlatSpec with ShouldMatchers {
//    "PluginInstance" should "not get initialized with null plugin" in {
//        evaluating(new PluginInstance(_plugin = null)) should produce [IllegalArgumentException]
//    }

    "PluginInstance" should "have sane getters and setters" in {
        val p: Plugin = new Plugin(_name = "MyPlugin")
        val pI: PluginInstance = new PluginInstance(_plugin = p)
        val param: StringParameter = new StringParameter(n = "Hello", defaultValue = "Jell-O")

        evaluating(pI.hasSetValueForParameter(null)) should produce[IllegalArgumentException]
        evaluating(pI.valueForParameter(null)) should produce[IllegalArgumentException]

        // Haven't added param to the plugin yet, these calls should end up with an exception
        evaluating(pI.hasSetValueForParameter(param)) should  produce[IllegalArgumentException]
        //evaluating(pI.setValueForParameter(param, param.createInstance())) should
        //                                                                produce[IllegalArgumentException]
        evaluating(pI.valueForParameter(param)) should  produce[IllegalArgumentException]


        // Adding the parameter to the plugin, the calls above should now work without throwing an exception
        p.addParameter(param)

        pI.hasSetValueForParameter(param) should be (false)
        assume(pI.valueForParameter(param).isEmpty)
//        pI.setValueForParameter(param, param.createInstance())
//        pI.hasSetValueForParameter(param) should be (true)
//        pI.valueForParameter(param).get.stringValue should be ("Jell-O")

    }

}*/

