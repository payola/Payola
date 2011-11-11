package s2js

import scala.tools.nsc.Global
import scala.tools.nsc.plugins.{Plugin, PluginComponent}

import java.io.File


class ScalaToJsPlugin(val global: Global) extends Plugin
{
    val name = "s2js"
    val description = "Scala to Javascript compiler plugin"
    val runsAfter = List("refchecks")
    val components = List[PluginComponent](new ScalaToJsComponent(this))

    var output = new File("")
    var debug = false

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

        debug = optionsMap.contains("debug")
    }

    def debugPrint(thing: Any) {
        if (debug) {
            println("[" + name + "] " + thing.toString)
        }
    }
}

