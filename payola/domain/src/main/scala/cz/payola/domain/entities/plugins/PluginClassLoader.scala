package cz.payola.domain.entities.plugins

import cz.payola.domain.entities.Plugin

class PluginClassLoader(pluginClassDirectory: java.io.File, parentClassLoader: java.lang.ClassLoader)
    extends java.net.URLClassLoader(Array[java.net.URL](pluginClassDirectory.toURI.toURL), parentClassLoader)
{
    def instantiatePlugin(pluginClassName: String): Plugin = {
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
