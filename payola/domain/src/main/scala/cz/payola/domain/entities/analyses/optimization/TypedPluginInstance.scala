package cz.payola.domain.entities.analyses.optimization

import cz.payola.domain.entities.analyses.PluginInstance
import cz.payola.domain.entities.analyses.Plugin

case class TypedPluginInstance[A <: Plugin](plugin: A, instance: PluginInstance)
