package cz.payola.domain

import cz.payola.domain.entities.plugins.PluginClassLoader
import cz.payola.domain.entities.plugins.compiler.PluginCompiler

trait PluginCompilerComponent
{
    val pluginCompiler: PluginCompiler

    val pluginClassLoader: PluginClassLoader
}
