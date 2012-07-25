package s2js.runtime.shared.rpc

trait RpcExceptionTrait
{
    val message: String
    val deepStackTrace: String
    val cause: Throwable

}
