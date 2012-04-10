package s2js.runtime.client.rpc

class Exception(override val message: String = "", cause: Exception = null) extends s2js.runtime.client.Exception(message, cause)
