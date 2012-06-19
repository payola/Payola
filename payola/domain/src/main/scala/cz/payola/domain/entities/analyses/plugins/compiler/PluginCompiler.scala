package cz.payola.domain.entities.analyses.plugins.compiler

import tools.nsc.{Global, Settings}
import java.util.UUID
import java.util.Properties
import scala.tools.nsc.io._
import scala.tools.nsc.reporters._

/**
  * A compiler of the analytical plugins.
  * @param libDirectory The directory with all libraries that may be used in the plugins.
  * @param pluginClassDirectory The output directory where the plugin class files are stored.
  */
class PluginCompiler(val libDirectory: java.io.File, val pluginClassDirectory: java.io.File)
{
    /**
      * Compiles the specified plugin from the source code.
      * @param pluginSourceCode The plugin scala source code.
      */
    def compile(pluginSourceCode: String) {
        // Create the temporary plugin source file.
        val pluginFileName = UUID.randomUUID.toString + ".scala"
        val pluginFile = new java.io.File(pluginFileName)
        new File(pluginFile).writeAll(pluginSourceCode)

        // Initialize the classpath.
        val paths = new Directory(libDirectory).files.map(_.path)
        val classpath = paths.mkString(java.io.File.pathSeparator)

        // Initialize the compiler settings.
        val settings = new Settings()
        settings.classpath.value = classpath
        settings.outdir.value = pluginClassDirectory.getAbsolutePath

        // Compile the plugin file.
        val reporter = new ConsoleReporter(settings)
        val compiler = new InternalCompiler(settings, reporter)
        val run = new compiler.Run()
        run.compile(List(pluginFileName))
        pluginFile.delete()

        if (reporter.hasErrors || reporter.hasWarnings) {
            throw new PluginCompilationException("TODO")
        }
    }

    private class InternalCompiler(settings: Settings, reporter: Reporter) extends Global(settings, reporter)
    {

    }
}
