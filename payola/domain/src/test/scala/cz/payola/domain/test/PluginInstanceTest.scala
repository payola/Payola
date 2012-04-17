package cz.payola.domain.test

import cz.payola.domain._
import cz.payola.domain.parameter._
import entities.{Plugin, PluginInstance}
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import collection.mutable.ArrayBuffer

class PluginInstanceTest extends FlatSpec with ShouldMatchers {
    "PluginInstance" should "not get initialized with null plugin" in {
        evaluating(new PluginInstance(null)) should produce [IllegalArgumentException]
    }

    "PluginInstance" should "have sane getters and setters" in {
        val p: Plugin = new Plugin("MyPlugin", new ArrayBuffer[Parameter[_]]())
        val pI: PluginInstance = new PluginInstance(p)
        val param: StringParameter = new StringParameter("Hello", "Jell-O")

        evaluating(pI.hasSetValueForParameter(null)) should produce[IllegalArgumentException]
        evaluating(pI.valueForParameter(null)) should produce[IllegalArgumentException]

        // Haven't added param to the plugin yet, these calls should end up with an exception
        evaluating(pI.hasSetValueForParameter(param)) should  produce[IllegalArgumentException]
        evaluating(pI.setValueForParameter(param, param.createInstance())) should
                                                                        produce[IllegalArgumentException]
        evaluating(pI.valueForParameter(param)) should  produce[IllegalArgumentException]


        // Adding the parameter to the plugin, the calls above should now work without throwing an exception
        p.addParameter(param)

        pI.hasSetValueForParameter(param) should be (false)
        assume(pI.valueForParameter(param).isEmpty)
        pI.setValueForParameter(param, param.createInstance())
        pI.hasSetValueForParameter(param) should be (true)
        pI.valueForParameter(param).get.stringValue should be ("Jell-O")
        
    }

}

