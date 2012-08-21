package cz.payola.domain.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.domain.entities.plugins.compiler._
import cz.payola.domain.entities.plugins.PluginClassLoader

class PluginCompilerSpec extends FlatSpec with ShouldMatchers
{
    val libDirectory = new java.io.File("lib")

    val pluginClassDirectory = new java.io.File("domain/target/scala-2.9.1/test-classes")

    val compiler = new PluginCompiler(libDirectory, pluginClassDirectory)

    val loader = new PluginClassLoader(pluginClassDirectory, getClass.getClassLoader)

    "Plugin compiler" should "compile simple plugins" in {
        val pluginInfo = compiler.compile(
            """
                package my.custom.plugin

                import scala.collection._
                import cz.payola.domain._
                import cz.payola.domain.entities._
                import cz.payola.domain.entities.plugins._
                import cz.payola.domain.entities.plugins.parameters._
                import cz.payola.domain.rdf._

                class DelayInSeconds(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
                    extends Plugin(name, inputCount, parameters, id)
                {
                    def this() = this("Time Delay in seconds", 1, List(new IntParameter("Delay", 1)), IDGenerator.newId)

                    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Option[Graph]], progressReporter: Double => Unit) = {
                        usingDefined(instance.getIntParameter("Delay")) { d =>
                            (1 to d).foreach { i =>
                                Thread.sleep(1000)
                                progressReporter(i.toDouble / d)
                            }
                            inputs(0).getOrElse(Graph.empty)
                        }
                    }
                }
            """)

        val plugin = loader.instantiatePlugin(pluginInfo.className)
        assert(pluginInfo.name == "Time Delay in seconds", "The plugin name is invalid.")
        assert(pluginInfo.name == plugin.name, "The plugin name doesn't match the name in the plugin info.")
        assert(plugin.inputCount == 1, "The plugin input count is invalid.")
        assert(plugin.parameters.length == 1, "The plugin parameter count is invalid.")
        assert(plugin.parameters.head.name == "Delay", "The plugin parameter is invalid.")
    }

    it should "throw exceptions when the compilation fails" in {
        try {
            val pluginInfo = compiler.compile(
                """
                    package my.custom.plugin

                    class MyPlugin(
                """)
            fail("The PluginCompilationException wasn't thrown.")
        } catch {
            case _: PluginCompilationException => // NOOP
            case _ => fail("The PluginCompilationException wasn't thrown.")
        }
    }
}

