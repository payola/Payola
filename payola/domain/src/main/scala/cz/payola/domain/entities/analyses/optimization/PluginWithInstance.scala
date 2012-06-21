package cz.payola.domain.entities.analyses.optimization

import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.plugins.PluginInstance

case class PluginWithInstance[A <: Plugin](plugin: A, instance: PluginInstance)
