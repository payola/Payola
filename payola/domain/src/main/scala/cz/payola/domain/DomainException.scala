package cz.payola.domain

import cz.payola.common.exception.PayolaException

class DomainException(message: String = "", cause: Throwable = null) extends PayolaException(message, cause)
