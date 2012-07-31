package cz.payola.domain

import cz.payola.common.PayolaException

class DomainException(message: String = "", cause: Throwable = null) extends PayolaException(message, cause)
