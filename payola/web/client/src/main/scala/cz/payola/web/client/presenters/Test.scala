package cz.payola.web.client.presenters

import s2js.adapters.js.browser._
import cz.payola.web.shared.RPCTester
import s2js.compiler.javascript
import s2js.runtime.shared.rpc

class Test
{

    def failTest(index: Int, description: String, reason: String) = {
        //TODO: implement String.format in JS
        //val failString = String.format("Test no. %d failed:\n[%s]\n\nReason: %s",index.toString(),description, reason)
        val failString = "Test no. "+index+" failed:\n["+description+"]\n\nReason: "+reason
        window.alert(failString)
    }

    def init() {

        if (!(RPCTester.procedure == 1)) {
            failTest(1, "Basic test on calling RPC without any parameter.", "Result is not equal to 1.")
        }
        if (!(RPCTester.testBoolean)) {
            failTest(2, "Basic test expecting Boolean as result.", "Result is not true.")
        }
        if (!(RPCTester.testString == "te\"st")) {
            failTest(3, "A parameterless call, String expected as result. The result contains quote to test escaping", "Result is not equal to te&quote;st.")
        }
        if (!(RPCTester.testParamString("abcd efgh") == "hgfe dcba")) {
            failTest(4, "A test with a String parameter. It expects a string to be returned, moreover, it should be the parameter, but reversed.", "Result is not equal to 'hgfe dcba'.")
        }
        if (!(RPCTester.testParamBoolean(false))) {
            failTest(5, "A test with a Boolean parameter. It expects a Boolean to be returned, moreover, it should be the negation of the parameter - true.", "Result is not true.")
        }
        if (!(RPCTester.testParamChar('A') == 'A')) {
            failTest(6, "Char echo.", "Result is not 'A'.")
        }
        if (!(RPCTester.testParamInt(2) == 4)) {
            failTest(7, "A test with a Int parameter. The expected result is the parameter doubled by 2.", "Result is not 4.")
        }
        if (!(RPCTester.testParamDouble(2.0) == 2.0)) {
            failTest(8, "A test with a Double parameter to examine the deserialization hell. The expected result is the parameter itself.", "Result is not 2.0.")
        }
        if (!(RPCTester.testParamArray(List(1,2,3)) == 6)){
            failTest(9, "A test with a List as a parameter. The expected result is a sum of the values in the list.", "Result is not 6.")
        }

        val test10Desc = "A test with a List as parameter. But ASYNC. The expected result is a sum of the values in the list."

        RPCTester.testParamArrayAsync(List(1,2,3)) {
            case 6 => // NOOP, success
            case _ => failTest(10, test10Desc, "Result is not 6.")
        } {throwable =>
            failTest(10, test10Desc, "Something went wrong on the server.")
        }

        if (!(RPCTester.testParamArrayDouble(List(1.1,2.2,3.3)) == 6.6)){
            failTest(11, "Trying to get a sum on a List[Double]", "Result is not 6.6.")
        }

        if (!(RPCTester.testParamArrayString(List("ab","cd","ef")) == "abcdef")){
            failTest(12, "Running a test with a list of Strings. Expecting to retreive concat.", "Result is not equal to 'abcdef'.")
        }
/*
        try
        {
            RPCTester.throwException
            window.alert("Exception was expected to be caught!")
        }catch{
            case e: RPCException => {
                window.alert("Yay!")
            }
        }*/

        val test13Desc = "Testing a rpc.Exception throw on a bad RPC call."

        try
        {
            testException
            failTest(13, test13Desc, "Exception was not thrown.")
        } catch {
            case e: rpc.Exception => window.alert("YES!")// NOOP, success
            case _ => failTest(13, test13Desc, "Exception was thrown, but is not of type rpc.Exception")
        }
    }

    @javascript("""
        s2js.runtime.client.rpc.Wrapper.callSync("hello", [], [])
    """)
    def testException = None
}
