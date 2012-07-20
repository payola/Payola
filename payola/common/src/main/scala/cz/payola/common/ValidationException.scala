package cz.payola.common

class ValidationException(val fieldName: String, message: String = "", cause: Throwable = null)
    extends PayolaException(message, cause)
