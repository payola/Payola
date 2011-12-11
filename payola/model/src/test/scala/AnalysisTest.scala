package cz.payola.model.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.model._

class AnalysisTest extends FlatSpec with ShouldMatchers {
    "Analysis" should "not be initialized with null user or name" in {
        val u: User = new User("Franta")
        evaluating(new Analysis(null, null)) should produce [IllegalArgumentException]
        evaluating(new Analysis("HelloWorld", null)) should produce [IllegalArgumentException]
        evaluating(new Analysis(null, u)) should produce [IllegalArgumentException]
        evaluating(new Analysis("", u)) should produce [IllegalArgumentException]
    }

    "Analysis" should "retain attributes passed in the constructor" in {
        val name: String = "HelloWorld"
        val u: User = new User("Franta")
        val a: Analysis = new Analysis(name, u)

        u.isOwnerOfAnalysis(a) && u.hasAccessToAnalysis(a) && a.owner == u && a.name == name
    }

    "Analysis" should "work correctly with appending and removing plugin instances" in {
        val name: String = "HelloWorld"
        val u: User = new User("Franta")
        val a: Analysis = new Analysis(name, u)
        a.pluginInstances.size should equal (0)

        val plugin: Plugin = new Plugin("MyPlugin")
        val instance1: PluginInstance = new PluginInstance(plugin)
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
