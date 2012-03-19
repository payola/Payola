package cz.payola.web.client.presenters

import s2js.adapters.js.browser._
import s2js.runtime.s2js.RPCException
import cz.payola.web.shared.RPCTester

class Test
{

    def init() {
        assert(RPCTester.procedure == 1)
        assert(RPCTester.testBoolean)
        assert(RPCTester.testString == "test")
        assert(RPCTester.testParamArray(Array(1,2,3)) == 6)
        assert(RPCTester.testParamString("abcd efgh") == "hgfe dcba")
        assert(RPCTester.testParamBoolean(false))
        assert(RPCTester.testParamChar('A') == 'A')
        assert(RPCTester.testParamInt(2) == 4)
        assert(RPCTester.testParamDouble(2.1111111) = 2.111111)
        
        try {
            RPCTester.testException
        } catch {
            case e: RPCException => {
                window.alert("Failed to call RPC. " + e.message)
            }
        }
        
    }
}
