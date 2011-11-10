package s2js

import scala.tools.nsc.{Global, Phase}
import scala.tools.nsc.plugins.{Plugin, PluginComponent}

import scala.collection.{ mutable => mu }

import java.io.{File, FileWriter, BufferedWriter}

class ScalaToJsPlugin (val global:Global) extends Plugin {

	val name = "s2js"
	val description = "Scala to Javascript compiler plugin"
	val runsAfter = List("refchecks")

	val components = List[PluginComponent](Component)

	var output = "."
    var input = ""
	
	override def processOptions(options:List[String], error:String=>Unit) {

        val optionsMap = options.foldLeft(Map.empty[String,String]) {
            (a, b) => a ++ Map(b.split(":").head -> b.split(":").last)
        }

        output = optionsMap.getOrElse("output", "")
        input = optionsMap.getOrElse("input", "")

		// validate
		if (output == "") error("You must provide an [output] option")
		if (input == "") error("You must provide an [input] option")
	}

	private object Component extends PluginComponent with ScalaToJsPrinter {

      val global = ScalaToJsPlugin.this.global
      val phaseName = ScalaToJsPlugin.this.name

      import global._

      val runsAfter = List("typer")

      def newPhase(prev:Phase) = new StdPhase(prev) {
            
        override def name = phaseName

        override def apply(unit:CompilationUnit) = {

          def needsProcessing(sym:Symbol):Boolean = input.split(";") exists { sym.fullName.startsWith(_) }

          //if(needsProcessing(unit.body.symbol)) {

            val packagePath = unit.body.symbol.fullName.replace('.', '/')
            val newFilePath = output + "/" + packagePath + "/" + unit.source.file.name.replace(".scala",".js")
            new File(newFilePath).getParentFile.mkdirs

            var stream = new FileWriter(newFilePath)
            var writer = new BufferedWriter(stream)

            writer write tree2string(unit.body)
            writer.close()
          //}
        }
      }
    }
}

