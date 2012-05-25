package cz.payola.domain.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.domain.entities.analyses.parameters.StringParameter
import cz.payola.domain.entities.analyses.Plugin

class PluginTest extends FlatSpec with ShouldMatchers {

    "Plugin" should "have sane getters and setters" in {
        val p: Plugin = new PseudoPlugin("MyPlugin")
        val param: StringParameter = new StringParameter("Hello", "")

        p.getParameter("Helo").isDefined should be (false)
        p.getParameter("Hello").isDefined should be (false)

        evaluating(p.name_=(null)) should produce[IllegalArgumentException]
        evaluating(p.name_=("")) should produce[IllegalArgumentException]

        p.name_=("NewName")
        p.name should be ("NewName")
    }

}

