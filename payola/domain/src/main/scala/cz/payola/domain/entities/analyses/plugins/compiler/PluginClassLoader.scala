package cz.payola.domain.entities.analyses.plugins.compiler

import java.net._
import cz.payola.domain.entities.analyses.Plugin

class PluginClassLoader(pluginClassDirectory: java.io.File, parentClassLoader: java.lang.ClassLoader)
    extends URLClassLoader(Array[URL](pluginClassDirectory.toURI.toURL), parentClassLoader)
{
    def getPlugin(pluginClassName: String): Plugin = {
        try {
            val pluginClass = loadClass(pluginClassName)
            pluginClass.newInstance match {
                case p: Plugin => p
                case _ => throw new PluginLoadingException("The specified class isn't a subclass of the Plugin class.")
            }
        } catch {
            case e: Exception => throw new PluginLoadingException(e.getMessage)
            case t => throw new PluginLoadingException("Unknown error occured during plugin loading (%s).".format(t))
        }
    }
}
