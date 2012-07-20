package cz.payola.domain.entities.plugins.compiler

import cz.payola.domain.entities.plugins.PluginException

class PluginCompilationException(message: String = "", cause: Throwable = null) extends PluginException(message, cause)
