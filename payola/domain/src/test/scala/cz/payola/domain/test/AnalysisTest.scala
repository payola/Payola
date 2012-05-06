package cz.payola.domain.test

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import cz.payola.domain._
import entities.{Analysis, Plugin, PluginInstance, User}

class AnalysisTest extends FlatSpec with ShouldMatchers {
/*    "Analysis" should "not be initialized with null user or name" in {
        val u: User = new User(_name = "Franta")
        evaluating(new Analysis(_name = null, _owner = null)) should produce [IllegalArgumentException]
        evaluating(new Analysis(_name = "HelloWorld", _owner = null)) should produce [IllegalArgumentException]
        evaluating(new Analysis(_name = null, _owner = u)) should produce [IllegalArgumentException]
        evaluating(new Analysis(_name = "", _owner = u)) should produce [IllegalArgumentException]
    }*/

    "Analysis" should "retain attributes passed in the constructor" in {
        val name: String = "HelloWorld"
        val u: User = new User(_name = "Franta")
        val a: Analysis = new Analysis(_name = name, _owner = u)

        u.isOwnerOfAnalysis(a) && u.accessibleAnalyses.contains(a) && a.owner == u && a.name == name
    }

    "Analysis" should "work correctly with appending and removing plugin instances" in {
        val name: String = "HelloWorld"
        val u: User = new User(_name = "Franta")
        val a: Analysis = new Analysis(_name = name, _owner = u)
        a.pluginInstances.size should equal (0)

        val plugin: Plugin = new Plugin(_name = "MyPlugin")
        val instance1: PluginInstance = new PluginInstance(_plugin = plugin)
        a.appendPluginInstance(instance1)
        a.pluginInstances.size should equal (1)

        // Try double-appending, the count should still be 1
        a.appendPluginInstance(instance1)
        a.pluginInstances.size should equal (1)

        // Try removing it
        a.removePluginInstance(instance1)
        a.pluginInstances.size should equal (0)

        // Try setting the instances
        a.setPluginInstances(Array[PluginInstance](instance1))
        a.pluginInstances.size should equal (1)
    }


}
