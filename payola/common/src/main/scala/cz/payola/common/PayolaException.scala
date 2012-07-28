package cz.payola.common

class PayolaException(val message: String = "", val cause: Throwable = null) extends Exception(message, cause)
