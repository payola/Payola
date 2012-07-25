package cz.payola.common.exception

class PayolaException(val message: String = "", val cause: Throwable = null) extends Exception(message, cause)
