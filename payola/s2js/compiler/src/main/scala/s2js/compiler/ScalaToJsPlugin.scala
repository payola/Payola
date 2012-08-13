package s2js.compiler

import java.io.File
import s2js.compiler.components.PackageDefCompiler
import scala.collection.mutable
import tools.nsc.plugins.{PluginComponent, Plugin}
import tools.nsc.io
import tools.nsc.{Phase, Global}

/** A Scala to JavaScript compiler plugin. */
class ScalaToJsPlugin(val global: Global) extends Plugin
{
    /** The name of this plugin. */
    val name = "s2js"

    /** A one-line description of the plugin. */
    val description = "Scala to Javascript compiler plugin"

    /** The components that this phase defines. */
    val components = List[PluginComponent](ScalaToJsComponent)

    /** The output directory where the compiled javascript files are stored. */
    private var outputDirectory = new File(".")

    /** Whether the output directory structure should correspond to the packages or not. */
    private var createPackageStructure = true

    /** A component that is part of the ScalaToJsPlugin. */
    private object ScalaToJsComponent extends PluginComponent
    {
        /** The global environment; overridden by instantiation in Global. */
        val global = ScalaToJsPlugin.this.global

        /** List of phase names, this phase should run after.  */
        val runsAfter = List[String]("refchecks")

        /** The name of the phase. */
        val phaseName = "s2js-phase"

        /** A Scala to JavaScript plugin phase. */
        private class ScalaToJsPhase(prev: Phase) extends StdPhase(prev)
        {
            /**
              * Executes the phase on the specified CompilationUnit.
              * @param unit The CompilationUnit to execute the phase on.
              */
            def apply(unit: global.CompilationUnit) {
                try {
                    if (unit.body.isInstanceOf[Global#PackageDef]) {
                        val packageDef = unit.body.asInstanceOf[Global#PackageDef]
                        val packageName = packageDef.symbol.fullName
                        val packagePath = if (createPackageStructure) packageName.replace('.', '/') else "."
                        val fileName = unit.source.file.name.replace(".scala", ".js")
                        val outputFile = new File(outputDirectory.getAbsolutePath + "/" + packagePath + "/" + fileName)
                        outputFile.getParentFile.mkdirs()

                        try {
                            val compiler = new PackageDefCompiler(global, unit.source.file, packageDef)
                            io.File(outputFile).writeAll(compiler.compile())
                        } catch {
                            case e: Exception => {
                                println(e)
                                throw e
                            }
                        }

                    } else {
                        throw new ScalaToJsException(
                            "The %s source file must contain a package definition.".format(unit.source.file.name)
                        )
                    }
                } catch {
                    case e: ScalaToJsException => global.error(e.errorMsg)
                }
            }
        }

        /**
          * The phase factory.
          * @param prev The previous phase.
          * @return The created phase.
          */
        def newPhase(prev: Phase): Phase = {
            new ScalaToJsPhase(prev)
        }
    }

    /**
      * Handles all plugin-specific options.
      * @param options The options passed to the plugin.
      */
    override def processOptions(options: List[String], error: String => Unit) {
        val optionsMap = new mutable.HashMap[String, String]
        options.foreach {option =>
            val index = option.indexOf(":")
            if (index > 0) {
                optionsMap += option.take(index) -> option.drop(index + 1)
            } else {
                optionsMap += option -> ""
            }
        }

        optionsMap.get("outputDirectory").foreach { o =>
            outputDirectory = new File(o)
        }
        optionsMap.get("createPackageStructure").foreach { c =>
            createPackageStructure = c == "true"
        }
    }
}
