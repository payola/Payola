package s2js

import scala.tools.nsc.Global
import scala.tools.nsc.Phase
import scala.tools.nsc.plugins.Plugin
import scala.tools.nsc.plugins.PluginComponent

import java.io.{FileWriter, BufferedWriter, File}


class ScalaToJsPlugin(val global: Global) extends Plugin
{
    val name = "s2js"
    val description = "Scala to Javascript compiler plugin"
    val components = List[PluginComponent](Component)

    /**The output directory where the compile javascript files are stored. */
    var output = new File("")

    var ignorePackages = false

    private object Component extends PluginComponent with PackageCompiler
    {
        val global = ScalaToJsPlugin.this.global

        import global._

        val runsAfter = List[String]("refchecks");
        val phaseName = ScalaToJsPlugin.this.name

        def newPhase(prev: Phase): Phase = {
            new ScalaToJsPhase(prev)
        }

        class ScalaToJsPhase(prev: Phase) extends StdPhase(prev)
        {
            override def name = {
                ScalaToJsPlugin.this.name
            }

            def apply(unit: CompilationUnit) {
                // Prepare the output file.
                val packagePath = if (ignorePackages) "" else unit.body.symbol.fullName.replace('.', '/') + "/"
                val outputFileName = unit.source.file.name.replace(".scala", ".js")
                val outputFile = new File(output.getAbsolutePath + "/" + packagePath + outputFileName)
                outputFile.getParentFile.mkdirs()

                // Compile the ast into js and write the result to the output file.
                val writer = new BufferedWriter(new FileWriter(outputFile))
                writer.write(compile(unit.body.asInstanceOf[PackageDef]))
                writer.close()
            }
        }

    }

    override def processOptions(options: List[String], error: String => Unit) {
        val optionsMap = options.foldLeft(Map.empty[String, String]) {
            (map, option) => {
                val index = option.indexOf(":")
                if (index > 0) {
                    map ++ Map(option.substring(0, index) -> option.slice(index + 1, option.length))
                } else {
                    map ++ Map(option -> "")
                }
            }
        }

        if (!optionsMap.contains("output")) {
            error("You must provide an [output] option")
        }
        output = new File(optionsMap.getOrElse("output", ""))

        if (optionsMap.contains("ignorePackages")) {
            ignorePackages = true
        }
    }
}

