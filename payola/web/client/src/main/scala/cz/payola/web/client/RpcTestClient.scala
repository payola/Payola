package cz.payola.web.client

import s2js.adapters.js.browser._
import cz.payola.web.shared.RpcTestRemote

object RpcTestClient
{
    def test() {
        window.alert(RpcTestRemote.foo(123, "xyz"))
    }
}
