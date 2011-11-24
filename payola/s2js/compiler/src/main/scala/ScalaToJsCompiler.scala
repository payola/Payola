package s2js.compiler

import tools.nsc.{Global, Settings}

class ScalaToJsCompiler(val classPath: String, val output: String, val ignorePackages: Boolean = false) {
    def compileFiles(sourceFiles: List[String]) {
        val settings = new Settings()
        settings.classpath.tryToSet(List(classPath))
        val options = (if (ignorePackages) List("ignorePackages") else Nil) ++ List("output:" + output)
        val compiler = new InternalCompiler(settings, options)

        val run = new compiler.Run()
        run.compile(sourceFiles);
    }

    private class InternalCompiler(settings: Settings, val options: List[String]) extends Global(settings) {
        val scalaToJsPlugin = new ScalaToJsPlugin(this)

        override protected def computeInternalPhases() {
            scalaToJsPlugin.processOptions(options, (err: String) => println(err))

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