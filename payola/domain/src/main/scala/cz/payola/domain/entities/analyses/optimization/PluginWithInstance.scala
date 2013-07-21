package cz.payola.domain.entities.analyses.optimization

import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.plugins.PluginInstance

/**
  * A plugin with its instance.
  * @param plugin The plugin.
  * @param instance The instance of the plugin.
  * @tparam A Type of the plugin.
  */
case class PluginWithInstance[A <: Plugin](plugin: A, instance: PluginInstance)
{
    //require(instance.plugin == plugin, "The plugin instance must correspond to the plugin.")
}
