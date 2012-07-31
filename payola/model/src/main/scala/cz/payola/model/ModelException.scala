package cz.payola.model

import cz.payola.common.PayolaException

class ModelException(message: String = "", cause: Throwable = null) extends PayolaException(message, cause)
