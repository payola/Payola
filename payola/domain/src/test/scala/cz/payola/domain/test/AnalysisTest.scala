package cz.payola.domain.test

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import cz.payola.domain.entities._
import analyses.parameters.StringParameterValue
import analyses.{Plugin, PluginInstance}

class AnalysisTest extends FlatSpec with ShouldMatchers {
    "Analysis" should "not be initialized with null user or name" in {
        val u: User = new User("Franta")
        evaluating(new Analysis("HelloWorld", null)) should produce [IllegalArgumentException]
        evaluating(new Analysis(null, Some(u))) should produce [IllegalArgumentException]
        evaluating(new Analysis("", Some(u))) should produce [IllegalArgumentException]
    }

    "Analysis" should "retain attributes passed in the constructor" in {
        val name: String = "HelloWorld"
        val u: User = new User("Franta")
        val a: Analysis = new Analysis(name, Some(u))

        u == a.owner && u.accessibleAnalyses.contains(a) && a.name == name
    }

    "Analysis" should "work correctly with appending and removing plugin instances" in {
        val name: String = "HelloWorld"
        val u: User = new User("Franta")
        val a: Analysis = new Analysis(name, Some(u))
        a.pluginInstances.size should equal (0)

        val plugin: Plugin = new PseudoPlugin("MyPlugin")
        val instance1: PluginInstance = new PluginInstance(plugin, List(plugin.getParameter("Time").get.createValue().asInstanceOf[StringParameterValue]))
        a.addPluginInstance(instance1)
        a.pluginInstances.size should equal (1)

        // Try double-appending, the count should still be 1
        evaluating(a.addPluginInstance(instance1)) should produce[IllegalArgumentException]
        a.pluginInstances.size should equal (1)

        // Try removing it
        a.removePluginInstance(instance1)
        a.pluginInstances.size should equal (0)
    }
}

