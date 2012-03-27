package cz.payola.web.client.presenters

import s2js.adapters.js.browser._
import s2js.runtime.s2js.RPCException
import cz.payola.web.shared.RPCTester
import s2js.compiler.javascript

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
        if (!(RPCTester.testParamArray(List(1,2,3)) == 6)){
            window.alert("fail test 9")
        }
        RPCTester.testParamArrayAsync(List(1,2,3))
        {x =>
            if (x != 6)
            {
                window.alert("fail test 9.5 (a)")
            }
        }
        {e => window.alert("fail test 9.5 (b)") }

        if (!(RPCTester.testParamArrayDouble(List(1.1,2.2,3.3)) == 6.6)){
            window.alert("fail test 10")
        }

        if (!(RPCTester.testParamArrayString(List("ab","cd","ef")) == "abcdef")){
            window.alert("fail test 11")
        }

        try
        {
            testException
            window.alert("Exception was expected to be caught!")
        }catch{
            case e: RPCException => {

            }
        }
    }

    @javascript("""
        s2js.RPCWrapper.callSync("hello",[])
    """)
    def testException = None
}
