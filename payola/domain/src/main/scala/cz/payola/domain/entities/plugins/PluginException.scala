package cz.payola.domain.entities.plugins

import cz.payola.domain.DomainException

class PluginException(message: String = "", cause: Throwable = null) extends DomainException(message, cause)
