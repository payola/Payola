package cz.payola.web.client.presenters

import s2js.adapters.js.browser._
import s2js.runtime.s2js.RPCException
import cz.payola.web.shared.RPCTester

class Test
{
    def init() {
        if (!(RPCTester.procedure == 1)) {
            window.alert("fail test 1");
        }
        if (!(RPCTester.testBoolean)) {
            window.alert("fail test 2");
        }
        if (!(RPCTester.testString == "test")) {
            window.alert("fail test 3");
        }
        //assert(RPCTester.testParamArray(Array(1,2,3)) == 6)
        if (!(RPCTester.testParamString("abcd efgh") == "hgfe dcba")) {
            window.alert("fail test 4");
        }
        if (!(RPCTester.testParamBoolean(false))) {
            window.alert("fail test 5");
        }
        if (!(RPCTester.testParamChar('A') == 'A')) {
            window.alert("fail test 6");
        }
        if (!(RPCTester.testParamInt(2) == 4)) {
            window.alert("fail test 7");
        }
        if (!(RPCTester.testParamDouble(2.0) == 2.0)) {
            window.alert("fail test 8");
        }
        /*
       try {
           RPCTester.testException
       } catch {
           case e: RPCException => {
               window.alert("Failed to call RPC. " + e.message)
           }
       } */
    }
}
