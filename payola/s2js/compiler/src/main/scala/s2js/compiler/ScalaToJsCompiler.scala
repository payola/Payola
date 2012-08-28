package s2js.compiler

import tools.nsc.{Global, Settings}

/**
  * A Scala to JavaScript compiler.
  */
class ScalaToJsCompiler(
    val classPath: String,
    val targetDirectory: String,
    val javaScriptDirectory: String,
    val createPackageStructure: Boolean = true)
{
    private val options = List(
        "outputDirectory:" + javaScriptDirectory,
        "createPackageStructure:" + createPackageStructure.toString
    )

    private val settings = new Settings()
    settings.classpath.value = classPath
    settings.outdir.value = targetDirectory

    /**
      * Compiles the specified Scala source files into JavaScript.
      * @param sourceFiles The Scala files to compile.
      */
    def compileFiles(sourceFiles: List[String]) {
        val compiler = new InternalCompiler(settings, options)
        val run = new compiler.Run()
        run.compile(sourceFiles)
    }

    /** An internal compiler that adds the ScalaToJsPlugin phases to the set of phases. */
    private class InternalCompiler(settings: Settings, val options: List[String]) extends Global(settings)
    {
        /** Add the compiler phases to the phases set. */
        override protected def computeInternalPhases() {
            super.computeInternalPhases()
            val scalaToJsPlugin = new ScalaToJsPlugin(this)
            scalaToJsPlugin.processOptions(options, s => ())
            scalaToJsPlugin.components.foreach(phasesSet += _)
        }
    }

}
