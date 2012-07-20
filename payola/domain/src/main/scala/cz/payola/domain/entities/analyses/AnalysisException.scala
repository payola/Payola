package cz.payola.domain.entities.analyses

import cz.payola.domain.DomainException

class AnalysisException(message: String = "", cause: Throwable = null) extends DomainException(message, cause)
