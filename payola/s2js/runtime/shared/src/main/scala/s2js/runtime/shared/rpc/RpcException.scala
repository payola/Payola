package s2js.runtime.shared.rpc

class RpcException(val message: String = "", val deepStackTrace: String = "", val cause: Throwable = null)
    extends scala.Exception(message, cause)
