package cz.payola.common.exception

class ValidationException(val fieldName: String, message: String = "", cause: Throwable = null)
    extends PayolaException(message, cause)
