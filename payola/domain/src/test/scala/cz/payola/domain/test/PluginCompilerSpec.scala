package cz.payola.domain.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.domain.entities.analyses.plugins.compiler._

class PluginCompilerSpec extends FlatSpec with ShouldMatchers
{
    val libDirectory = new java.io.File("lib")

    val pluginClassDirectory = new java.io.File("domain/target/scala-2.9.1/test-classes")

    val compiler = new PluginCompiler(libDirectory, pluginClassDirectory)

    val loader = new PluginClassLoader(pluginClassDirectory, getClass.getClassLoader)

    "Plugin compiler" should "compile simple plugins" in {
        compiler.compile(
            """
                package my.custom.plugin

                import scala.collection.immutable
                import cz.payola.domain._
                import cz.payola.domain.entities.analyses._
                import cz.payola.domain.entities.analyses.parameters._
                import cz.payola.domain.rdf._

                class MyPlugin(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
                    extends Plugin(name, inputCount, parameters, id)
                {
                    def this() = {
                        this("Custom plugin", 123, List(new StringParameter("Custom plugin param", "")), IDGenerator.newId)
                    }

                    def evaluate(instance: PluginInstance, inputs: collection.IndexedSeq[Option[Graph]],
                        progressReporter: Double => Unit) = {
                        Graph.empty
                    }
                }
            """)

        val plugin = loader.getPlugin("my.custom.plugin.MyPlugin")
        assert(plugin.name == "Custom plugin", "The plugin name is invalid.")
        assert(plugin.inputCount == 123, "The plugin input count is invalid.")
        assert(plugin.parameters.length == 1, "The plugin parameter count is invalid.")
        assert(plugin.parameters.head.name == "Custom plugin param", "The plugin parameter is invalid.")
    }

    it should "throw exceptions when the compilation fails" in {
        try {
            val plugin = compiler.compile(
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

