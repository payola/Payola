package controllers.helpers

import play.api.mvc.Request
import cz.payola.scala2json.JSONSerializer
import controllers.InvocationInfo
import cz.payola.scala2json.classes.SimpleSerializationClass
import cz.payola.scala2json.rules.BasicSerializationRule
import s2js.runtime.client.rpc

class RPCDispatcher(jsonSerializer: JSONSerializer)
{

    /** Graph rules **/
    loadGraphSerializationRules


    /**
      * Processes the given HTTP request and returns a response. The expected result is a serialized JSON object. See
      * s2js.runtime.s2js.RPCWrapper to see, how a object is deserialized to a JSON string and for more information
      * on a possibility of a custom deserialisation (currently not available).
      *
      * It expects a classic form/www-data POST request with a mandatory field named "method". It maps the request to
      * a Scala Map[String, Seq[String] ] instance which is transformed to a typed parameters list. The list is used
      * to invoke a method named with the value of the POST field "method".
      *
      * @param request HTTP request
      * @param asynchronous Whether the call is ment to be synchronous (false) or asynchronous (true)
      * @return JSON-encoded response object
      */
    def dispatchRequest(params: Map[String, Seq[String]], asynchronous: Boolean = false): String = {

        // get the name of the method
        // when empty, throw an exception
        val fqdn = params.get("method").flatMap(_.headOption).getOrElse(throw new Exception("TODO"))

        // sort the params list with the parameter name (presumably a ordering-index - 1,2,3,...)
        params.toList.sortBy {
            _._2.head
        }
        val paramTypesJson = params.getOrElse("paramTypes", List("[]")).head
        val paramTypes = util.parsing.json.JSON.parseFull(paramTypesJson)

        // the map keys are now irellevant, continue with values only
        val paramList = params.-("method").-("paramTypes").values

        // split the method names with "." to get package name and the name of the method without package
        // beware of the leading dot
        val fqdnParts = fqdn.splitAt(fqdn.lastIndexOf("."))

        // call the remote method synchronously or asynchronously - depends on the asynchronous parameter
        val result = asynchronous match {
            case true => invokeAsync(fqdnParts._1, fqdnParts._2.stripPrefix("."), paramList,
                paramTypes.get.asInstanceOf[Seq[String]])
            case false => invoke(fqdnParts._1, fqdnParts._2.stripPrefix("."), paramList,
                paramTypes.get.asInstanceOf[Seq[String]])
        }

        // return remote method call result
        result
    }

    /**
      * Invokes the remote method asynchronously. The remote method is designated with objectName, methodName.
      *
      * It internally calls the RPCActionExecutor, which is a Scala actor to avoid errors when the underlying
      * remote method calls the RPC successCallback in an nested thread. The callback is in-place-defined in
      * this method - it just sends a message to the RPCActionExecutor actor to tell him what to return.
      *
      * Despite the name of the method, the method is synchronous.
      *
      * The method adds two parameters into the parameter list - success callback and fail callback - in this order.
      * Therefore those callbacks will be always the last two parameters of a remote asynchronous method.
      *
      * @param objectName Name of the remote object containing the remote method
      * @param methodName Name of the remote method you want to call
      * @param params List of parameters - Sequences of Strings
      * @return Response encoded into JSON string
      */
    private def invokeAsync(objectName: String, methodName: String, params: Iterable[Seq[String]],
        paramTypes: Seq[String]): String = {

        val dto = getReflectionObjects(objectName, methodName)

        val paramsSize = params.size
        // allocate an array of objects for the parameters (adding 2 for callbacks)
        val paramArray = constructParamArray(paramsSize, 2, params, dto.methodToRun, paramTypes)

        val result = executeWithActors(paramArray, paramsSize, dto)

        val serialized = jsonSerializer.serialize(result)
        serialized
    }

    /**
      * Invokes the remote method synchronously.
      * @param objectName Name of the remote object containing the remote method
      * @param methodName Name of the remote method you want to call
      * @param params List of parameters - Sequences of Strings
      * @return Response encoded into JSON string
      */
    private def invoke(objectName: String, methodName: String, params: Iterable[Seq[String]],
        paramTypes: Seq[String]): String = {

        val dto = getReflectionObjects(objectName, methodName)

        // update each parameter and replace it with its properly typed representation
        val paramArray = constructParamArray(params.size, 0, params, dto.methodToRun, paramTypes)

        // invoke the remote method (!? for synchronous behaviour)
        val result = dto.methodToRun.invoke(dto.runnableObj, paramArray: _*)

        val serialized = jsonSerializer.serialize(result)
        serialized
    }

    private def constructParamArray(paramsSize: Int, extraSpace: Int, params: Iterable[Seq[String]],
        methodToRun: java.lang.reflect.Method, paramTypes: Seq[String]) = {
        val paramArray = new Array[java.lang.Object](paramsSize + extraSpace)

        // update each parameter and replace it with its properly typed representation
        var i = 0
        params.foreach(x => {
            paramArray.update(i, parseParam(x, methodToRun.getParameterTypes.apply(i), paramTypes.apply(i)))
            i = i + 1
        })

        paramArray
    }

    private def getReflectionObjects(objectName: String, methodName: String) = {
        // while objects are not really a Java thing, they are compiled into static classes named with trailing $ sign
        val clazz = Class.forName(objectName + "$"); // TODO: use Scala reflection when released

        // get the desired method on the object
        val methodOption = clazz.getMethods.find(_.getName == methodName)
        // if the method is not defined, throw an Exception
        if (!methodOption.isDefined) {
            throw new Exception
        }

        // "objectify" the desired mezhod to be able to invoke it later
        // this is a very ugly Java stuff, but Scala is not able to do this at the time
        val methodToRun: java.lang.reflect.Method = methodOption.getOrElse(null)
        val runnableObj = clazz.getField("MODULE$").get(objectName)

        val dto = new InvocationInfo(methodToRun, clazz, runnableObj)
        dto
    }

    private def executeWithActors(paramArray: Array[java.lang.Object], paramsSize: Int, dto: InvocationInfo) = {
        // create and start the actor
        val executor = new RPCActionExecutor()
        executor.start()

        // add param - success callback
        paramArray.update(paramsSize, {x: Any => executor ! new RPCReply(x)})
        // add param - fail callback - takes only Throwable as a parameter
        paramArray.update(paramsSize + 1, {x: Throwable => executor ! new RPCReply(x)})

        // invoke the remote method (!? for synchronous behaviour)
        val result = executor !? RPCActionMessage(dto.methodToRun, dto.runnableObj, paramArray)
        result
    }

    /**
      * The method parses the given sequence of strings into a typed parameter. The type is passed
      * as the paramType parameter.
      *
      * If the parameter is something simple (Bool, Char, Int, Long, ...), the head of the sequence is
      * parsed into the result. If it is an array, all of them are recursively parsed.
      *
      * @param input Sequence of string values to be transformed into a object representation
      * @param paramType Type of the parsed parameter
      * @return typed parameter
      */
    private def parseParam(input: Seq[String], paramType: Class[_], paramTypeClient: String): java.lang.Object = {
        if (paramType.getName.startsWith("scala.collection")) {
            parseSequence(input, paramType, paramTypeClient)
        } else {
            paramType.getName match {
                case "Boolean" => java.lang.Boolean.parseBoolean(input.head): java.lang.Boolean
                case "java.lang.Boolean" => java.lang.Boolean.parseBoolean(input.head): java.lang.Boolean
                case "boolean" => java.lang.Boolean.parseBoolean(input.head): java.lang.Boolean
                case "Int" => java.lang.Integer.parseInt(input.head): java.lang.Integer
                case "int" => java.lang.Integer.parseInt(input.head): java.lang.Integer
                case "char" => input.head.charAt(0): java.lang.Character
                case "Char" => input.head.charAt(0): java.lang.Character
                case "java.lang.Character" => input.head.charAt(0): java.lang.Character
                case "Double" => java.lang.Double.parseDouble(input.head): java.lang.Double
                case "double" => java.lang.Double.parseDouble(input.head): java.lang.Double
                case "java.lang.Double" => java.lang.Double.parseDouble(input.head): java.lang.Double
                case "Float" => java.lang.Float.parseFloat(input.head): java.lang.Float
                case "float" => java.lang.Float.parseFloat(input.head): java.lang.Float
                case "java.lang.Float" => java.lang.Float.parseFloat(input.head): java.lang.Float
                case _ => input.head.toString: java.lang.String
            }
        }
    }

    private def parseSequence(input: Seq[String], paramType: Class[_], paramTypeClient: String): java.lang.Object = {
        val seqString = input.head

        if (paramTypeClient.endsWith("[scala.Int]")) {
            util.parsing.json.JSON.perThreadNumberParser = {input: String => input.toInt}
        } else if (paramTypeClient.endsWith("[scala.Float]")) {
            util.parsing.json.JSON.perThreadNumberParser = {input: String => input.toFloat}
        } else if (paramTypeClient.endsWith("[scala.Short]")) {
            util.parsing.json.JSON.perThreadNumberParser = {input: String => input.toShort}
        } else if (paramTypeClient.endsWith("[scala.Double]")) {
            util.parsing.json.JSON.perThreadNumberParser = {input: String => input.toDouble}
        }

        val collection = util.parsing.json.JSON.parseFull(seqString).get.asInstanceOf[Seq[AnyVal]]

        collection
    }

    private def loadGraphSerializationRules = {
        val graphClass = new SimpleSerializationClass(classOf[cz.payola.common.rdf.Graph])
        val graphRule = new BasicSerializationRule(Some(classOf[cz.payola.common.rdf.Graph]))
        jsonSerializer.addSerializationRule(graphClass, graphRule)

        val edgeClass = new SimpleSerializationClass(classOf[cz.payola.common.rdf.Edge])
        val edgeRule = new BasicSerializationRule(Some(classOf[cz.payola.common.rdf.Edge]))
        jsonSerializer.addSerializationRule(edgeClass, edgeRule)

        val literalNodeClass = new SimpleSerializationClass(classOf[cz.payola.common.rdf.LiteralVertex])
        val literalNodeRule = new BasicSerializationRule(Some(classOf[cz.payola.common.rdf.LiteralVertex]))
        jsonSerializer.addSerializationRule(literalNodeClass, literalNodeRule)

        val identifiedNodeClass = new SimpleSerializationClass(classOf[cz.payola.common.rdf.IdentifiedVertex])
        val identifiedNodeRule = new BasicSerializationRule(Some(classOf[cz.payola.common.rdf.IdentifiedVertex]))
        jsonSerializer.addSerializationRule(identifiedNodeClass, identifiedNodeRule)
    }
}
