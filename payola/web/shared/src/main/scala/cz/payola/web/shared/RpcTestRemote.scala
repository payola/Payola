package cz.payola.web.shared

@scala.remote
object RpcTestRemote
{
    def foo(bar: Int, baz: String): Int = bar * baz.length
}
