package s2js.compiler

import tools.nsc.{Global, Settings}

/**
  * A Scala to JavaScript compiler.
  */
class ScalaToJsCompiler(val classPath: String, val outputDirectory: String, val createPackageStructure: Boolean = true)
{
    /**
      * Compiles the specified Scala source files into JavaScript.
      * @param sourceFiles The Scala files to compile.
      */
    def compileFiles(sourceFiles: List[String]) {
        val options = List(
            "outputDirectory:" + outputDirectory,
            "createPackageStructure:" + createPackageStructure.toString
        )
        val settings = new Settings()
        settings.classpath.tryToSet(List(classPath))

        val compiler = new InternalCompiler(settings, options)
        val run = new compiler.Run()
        run.compile(sourceFiles)
    }

    /** An internal compiler that adds the ScalaToJsPlugin phases to the set of phases. */
    private class InternalCompiler(settings: Settings, val options: List[String]) extends Global(settings)
    {
        /** Add the compiler phases to the phases set. */
        override protected def computeInternalPhases() {
            val scalaToJsPlugin = new ScalaToJsPlugin(this)
            scalaToJsPlugin.processOptions(options, s => ())

            phasesSet += syntaxAnalyzer
            phasesSet += analyzer.namerFactory
            phasesSet += analyzer.packageObjects
            phasesSet += analyzer.typerFactory
            phasesSet += superAccessors
            phasesSet += pickler
            phasesSet += refchecks
            scalaToJsPlugin.components.foreach(phasesSet += _)
        }
    }

}
