package cz.payola.domain.test

import cz.payola.domain.entities.analyses._
import cz.payola.domain.entities.analyses.parameters._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.domain.rdf.Graph

class PseudoPlugin(name: String) extends Plugin(name, 1, List(new StringParameter("Time", ""))) {
    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Option[Graph]], progressReporter: Double => Unit) = {
        Graph.empty
    }
}

class PluginInstanceTest extends FlatSpec with ShouldMatchers {
//    "PluginInstance" should "not get initialized with null plugin" in {
//        evaluating(new PluginInstance(_plugin = null)) should produce [IllegalArgumentException]
//    }

    "PluginInstance" should "have sane getters and setters" in {
        val p: Plugin = new PseudoPlugin("MyPlugin")

        assume(p.getParameter(null).isEmpty)
        //evaluating(p.getParameter(null)) should produce [IllegalArgumentException]

        p.getParameter("Hello").isDefined should be (false)
        val pl: PluginInstance = new PluginInstance(p, List(p.getParameter("Time").get.createValue().asInstanceOf[StringParameterValue]))

        p.getParameter("Time").isDefined should  be (true)
        assume(pl.getParameter("Time").get == "")


        val spv = new StringParameterValue(p.getParameter("Time").get.asInstanceOf[StringParameter], "")
        evaluating(pl.setParameter(spv, "00:00:00")) should produce[IllegalArgumentException]

        pl.setParameter("Time", "00:00:00")
        assume(pl.getParameter("Time").isDefined)

        pl.getParameter("Time").get should equal ("00:00:00")
    }

}

