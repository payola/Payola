package s2js.runtime.shared.rpc

class Exception(override val message: String, val deepStackTrace: String = "", cause: Exception = null)
    extends s2js.runtime.shared.Exception(message, cause)
