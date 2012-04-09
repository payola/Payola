package s2js.runtime.client.rpc

import collection.mutable.ArrayBuffer
import s2js.compiler.javascript
import s2js.adapters.js.browser._

private object Wrapper
{
    val deserializer = new Deserializer()

    val parameterSeparator = "&"

    def callSync(procedureName: String, parameters: ArrayBuffer[Any], parameterTypes: ArrayBuffer[String]): Any = {
        val request = createXmlHttpRequest("/RPC", isAsync = false)
        request.send(createRequestBody(procedureName, parameters, parameterTypes))
        processRequestResult(request, _ => (), throwable => throw throwable)
    }

    def callAsync(procedureName: String, parameters: ArrayBuffer[Any], parameterTypes: ArrayBuffer[String],
        successCallback: (Any => Unit), exceptionCallback: (Throwable => Unit)) {

        val request = createXmlHttpRequest("/RPC/async", isAsync = false)
        request.onreadystatechange = () => {
            if (request.readyState == 4) {
                processRequestResult(request, successCallback, exceptionCallback)
            }
        }
        request.send(createRequestBody(procedureName, parameters, parameterTypes))
    }

    private def createXmlHttpRequest(controllerUrl: String, isAsync: Boolean): XMLHttpRequest = {
        val request = if (s2js.runtime.client.isClassDefined("XMLHttpRequest")) {
            new XMLHttpRequest()
        } else {
            new ActiveXObject("Msxml2.XMLHTTP")
        }

        request.open("POST", controllerUrl, isAsync)
        request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded")
        request
    }

    private def processRequestResult(request: XMLHttpRequest, onSuccess: (Any => Unit),
        onException: (Throwable => Unit)): Any = {

        val result = if (request.readyState == 4 && request.status == 200) {
            deserializer.deserialize(eval("(" + request.responseText + ")"))
        } else {
            new Exception("RPC call exited with status code " + request.status + ".")
        }

        result match {
            case throwable: Throwable => {
                onException(throwable)
                throwable
            }
            case value => {
                onSuccess(value)
                value
            }
        }
    }

    private def createRequestBody(procedureName: String, parameters: ArrayBuffer[Any],
        parameterTypeNames: ArrayBuffer[String]): String = {

        // Append the procedure name to the request body.
        val requestBody = new ArrayBuffer[String]()
        requestBody += "method=" + procedureName

        // Append the parameter types to the request body.
        requestBody += parameterSeparator + "paramTypes="
        requestBody += encodeURIComponent(parameterTypeNames.map(jsonEscapeAndQuote).mkString("[", ",", "]"))

        // Append the parameters to the request body.
        var index = -1
        parameters.foreach {parameterValue =>
            index += 1
            requestBody += parameterSeparator + index + "="
            requestBody += encodeURIComponent(processParameter(parameterTypeNames(index), parameterValue))
        }

        requestBody.mkString
    }

    private def processParameter(typeName: String, value: Any): String = {
        value match {
            case s: String => s
            case items: Seq[_] => {
                val escapedItems: Seq[String] =
                    if (typeName.endsWith("[scala.String]") || typeName.endsWith("[java.lang.String]")) {
                        items.map(item => jsonEscapeAndQuote(item.toString))
                    } else {
                        items.map(_.toString)
                    }
                escapedItems.mkString("[", ",", "]")
            }
            case x => x.toString
        }
    }

    private def jsonEscapeAndQuote(value: String): String = "\"" + jsonEscape(value) + "\""

    @javascript("""
        return value.replace('\\', '\\\\').replace('"', '\\"').replace("'", "\\'");
    """)
    private def jsonEscape(value: String): String = ""
}
