package s2js

import org.scalatest.BeforeAndAfterAll

import org.scalatest.fixture.{
    FixtureSpec, ConfigMapFixture
}

import scala.tools.nsc.{Global, Settings, CompilerCommand}
import scala.tools.nsc.reporters.{ConsoleReporter, Reporter}
import scala.tools.nsc.plugins.{Plugin, PluginComponent}

import java.io.StringWriter

class S2JSPluginSpec extends FixtureSpec with ConfigMapFixture with BeforeAndAfterAll {
  
    def getResource(name:String):String = {
        return Thread.currentThread.getContextClassLoader.getResource(name).getPath.toString
    }

    override def beforeAll(configMap: Map[String, Any]) {
        val dir = new java.io.File(configMap("output").toString)
        dir.mkdir
    }

    it("should compile scala to javascript") { configMap =>

        val settings = new Settings

        settings.classpath.tryToSet(List(configMap("cp").toString))
        settings.d.tryToSet(List(configMap("output").toString))

        val reporter = new ConsoleReporter(settings)

        val files = List(
            getResource("p2/a/package.scala"))

        val command = new CompilerCommand(files, settings) {
            override val cmdName = "runs2js"
        }

        val options = List("output:"+configMap("output"), "input:p2")

        val runner = new Global(settings, reporter) {

            override protected def computeInternalPhases() {

                val plugin = new ScalaToJsPlugin(this)

                plugin.processOptions(options, (err:String) => println(err))

                phasesSet += syntaxAnalyzer					
                phasesSet += analyzer.namerFactory			
                phasesSet += analyzer.packageObjects		
                phasesSet += analyzer.typerFactory

                for (phase <- plugin.components) {
                    phasesSet += phase
                }
                
                phasesSet += superAccessors			// add super accessors
                phasesSet += pickler				// serialize symbol tables
                phasesSet += refchecks				// perform reference and override checking, translate nested objects
            }
        }

        val run = new runner.Run
        run.compile(command.files)
    }
}

// vim: set ts=4 sw=4 et:
