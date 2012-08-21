package controllers.helpers

import controllers.InvocationInfo
import scala.collection.mutable
import cz.payola.domain.entities.User
import s2js.runtime.shared.rpc.RpcException

class RPCDispatcher(jsonSerializer: RPCSerializer)
{
    /**
     * Processes the given HTTP request and returns a response. The expected result is a serialized JSON object. See
     * s2js.runtime.s2js.RPCWrapper to see, how a object is deserialized to a JSON string and for more information
     * on a possibility of a custom deserialisation (currently not available).
     *
     * It expects a classic form/www-data POST request with a mandatory field named "method". It maps the request to
     * a Scala Map[String, Seq[String] ] instance which is transformed to a typed parameters list. The list is used
     * to invoke a method named with the value of the POST field "method".
     *
     * @param params request parameters
     * @param asynchronous Whether the call is ment to be synchronous (false) or asynchronous (true)
     * @return JSON-encoded response object
     */
    def dispatchRequest(params: Map[String, Seq[String]], asynchronous: Boolean = false, user: Option[User]): String = {
        // get the name of the method
        // when empty, throw an exception
        val fqdn = params.get("method").flatMap(_.headOption).getOrElse(throw new RpcException("TODO"))

        // sort the params list with the parameter name (presumably a ordering-index - 1,2,3,...)
        params.toList.sortBy {
            _._2.head
        }
        val paramTypesJson = params.getOrElse("paramTypes", List("[]")).head
        val paramTypes = util.parsing.json.JSON.parseFull(paramTypesJson)

        // the map keys are now irellevant, continue with values only
        //val paramList = params.-("method").-("paramTypes").values

        val paramBuffer = mutable.Buffer.empty[Seq[String]]

        val limit = params.values.size - 2
        var i = 0
        while (i < limit) {
            paramBuffer.append(params(i.toString()))
            i += 1
        }

        val paramList = paramBuffer.toList

        // split the method names with "." to get package name and the name of the method without package
        // beware of the leading dot
        val fqdnParts = fqdn.splitAt(fqdn.lastIndexOf("."))

        // call the remote method synchronously or asynchronously - depends on the asynchronous parameter
        val result = asynchronous match {
            case true => invokeAsync(fqdnParts._1, fqdnParts._2.stripPrefix("."), paramList,
                paramTypes.get.asInstanceOf[Seq[String]], user)
            case false => invoke(fqdnParts._1, fqdnParts._2.stripPrefix("."), paramList,
                paramTypes.get.asInstanceOf[Seq[String]], user)
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
        paramTypes: Seq[String], user: Option[User]): String = {
        val dto = getReflectionObjects(objectName, methodName, user)

        val paramsSize = params.size

        val extraSpace = if (dto.methodIsSecured) {
            3
        } else {
            2
        }

        // allocate an array of objects for the parameters (adding 2 for callbacks)
        val paramArray = constructParamArray(paramsSize, extraSpace, params, dto.methodToRun, paramTypes)

        conditionallySetAuthorizationInfo(dto, paramArray, paramArray.size - extraSpace, user)

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
        paramTypes: Seq[String], user: Option[User]): String = {
        val dto = getReflectionObjects(objectName, methodName, user)

        val extraSpace = if (dto.methodIsSecured) {
            1
        } else {
            0
        }

        // update each parameter and replace it with its properly typed representation
        val paramArray = constructParamArray(params.size, extraSpace, params, dto.methodToRun, paramTypes)

        conditionallySetAuthorizationInfo(dto, paramArray, paramArray.size - extraSpace, user)

        // invoke the remote method (!? for synchronous behaviour)
        val result = dto.methodToRun.invoke(dto.runnableObj, paramArray: _*)

        val serialized = jsonSerializer.serialize(result)
        serialized
    }

    private def conditionallySetAuthorizationInfo(dto: InvocationInfo, paramArray: Array[Object], paramIndex: Int,
        user: Option[User]) {
        if (dto.methodIsSecured) {
            if (dto.authorizationRequired) {
                paramArray.update(paramIndex, user.get)
            } else {
                paramArray.update(paramIndex, user)
            }
        }
    }

    /**
     *
     * @param paramsSize
     * @param extraSpace
     * @param params
     * @param methodToRun
     * @param paramTypes
     * @return
     */
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

    /**
     *
     * @param objectName
     * @param methodName
     * @return
     */
    private def getReflectionObjects(objectName: String, methodName: String, user: Option[User]) = {
        // while objects are not really a Java thing, they are compiled into static classes named with trailing $ sign
        try {
            val clazz = Class.forName(objectName + "$");

            // get the desired method on the object
            val methodOption = clazz.getMethods.find(_.getName == methodName)
            // if the method is not defined, throw an RpcException
            if (!methodOption.isDefined) {
                throw new RpcException
            }

            // "objectify" the desired mezhod to be able to invoke it later
            // this is a very ugly Java stuff, but Scala is not able to do this at the time
            val methodToRun: java.lang.reflect.Method = methodOption.getOrElse(null)
            val runnableObj = clazz.getField("MODULE$").get(objectName)

            if (!isRemoteMethod(clazz, methodToRun)) {
                throw new java.lang.ClassNotFoundException
            }

            var authorizedUser: Option[User] = None
            val methodIsSecured = isSecuredMethod(clazz, methodToRun)
            val authorizationRequired = isAuthorizationRequired(methodIsSecured, methodToRun)

            if (methodIsSecured) {
                if (authorizationRequired) {
                    checkAuthorization(user)
                }
                authorizedUser = user
            }

            val dto = new InvocationInfo(methodToRun, clazz, runnableObj, methodIsSecured, authorizationRequired,
                authorizedUser)
            dto
        } catch {
            case e: java.lang.ClassNotFoundException => throw new RpcException("Invalid remote object name.")
        }
    }

    def lastParameterIsOfTypeUser(methodToRun: java.lang.reflect.Method): Boolean = {
        methodToRun.getParameterTypes.find {
            _.getName.equals("cz.payola.domain.entities.User")
        }.isDefined
    }

    def isAuthorizationRequired(methodIsSecured: Boolean, methodToRun: java.lang.reflect.Method): Boolean = {
        if (!methodIsSecured) {
            false
        } else {
            lastParameterIsOfTypeUser(methodToRun)
        }
    }

    def isAnnotationPresent(annotations: Array[java.lang.annotation.Annotation],
        authorizationClassName: String): Boolean = {
        return annotations.find { a => a.annotationType().getName().equals(authorizationClassName)}.isDefined
    }

    def isRemoteMethod(clazz: java.lang.Class[_], methodToRun: java.lang.reflect.Method): Boolean = {
        val classAnotations = clazz.getAnnotations

        if (isAnnotationPresent(classAnotations, "s2js.compiler.remote")) {
            true
        } else {
            false
        }
    }

    def isSecuredMethod(clazz: java.lang.Class[_], methodToRun: java.lang.reflect.Method): Boolean = {
        val classAnotations = clazz.getAnnotations

        if (isAnnotationPresent(classAnotations, "s2js.compiler.secured")) {
            true
        } else {
            val methodAnnotations = methodToRun.getAnnotations()
            if (isAnnotationPresent(methodAnnotations, "s2js.compiler.secured")) {
                true
            } else {
                false
            }
        }
    }

    def checkAuthorization(user: Option[User]) = {
        if (!user.isDefined) {
            throw new RpcException("Not authorized.")
        }
    }

    /**
     *
     * @param paramArray
     * @param paramsSize
     * @param dto
     * @return
     */
    private def executeWithActors(paramArray: Array[java.lang.Object], paramsSize: Int, dto: InvocationInfo) = {
        // create and start the actor
        val executor = new RPCActionExecutor()
        executor.start()

        // invoke the remote method (!? for synchronous behaviour)
        val result = executor !? RPCActionMessage(dto.methodToRun, dto.runnableObj, paramArray)

        result match {
            case resultMessage: ActionExecutorSuccess => resultMessage.result
            case errorMessage: ActionExecutorError => throw errorMessage.error
        }
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
            paramType.getName.toLowerCase match {
                case "java.lang.boolean" => java.lang.Boolean.parseBoolean(input.head): java.lang.Boolean
                case "boolean" => java.lang.Boolean.parseBoolean(input.head): java.lang.Boolean
                case "int" => java.lang.Integer.parseInt(input.head): java.lang.Integer
                case "char" => input.head.charAt(0): java.lang.Character
                case "java.lang.character" => input.head.charAt(0): java.lang.Character
                case "double" => java.lang.Double.parseDouble(input.head): java.lang.Double
                case "java.lang.double" => java.lang.Double.parseDouble(input.head): java.lang.Double
                case "float" => java.lang.Float.parseFloat(input.head): java.lang.Float
                case "java.lang.float" => java.lang.Float.parseFloat(input.head): java.lang.Float
                case "long" => java.lang.Long.parseLong(input.head): java.lang.Long
                case "java.lang.long" => java.lang.Long.parseLong(input.head): java.lang.Long
                case _ => input.head.toString: java.lang.String
            }
        }
    }

    /**
     *
     * @param input
     * @param paramType
     * @param paramTypeClient
     * @return
     */
    private def parseSequence(input: Seq[String], paramType: Class[_], paramTypeClient: String): java.lang.Object = {
        val seqString = input.head

        if (paramTypeClient.endsWith("[scala.Int]")) {
            util.parsing.json.JSON.perThreadNumberParser = { input: String => input.toInt}
        } else if (paramTypeClient.endsWith("[scala.Float]")) {
            util.parsing.json.JSON.perThreadNumberParser = { input: String => input.toFloat}
        } else if (paramTypeClient.endsWith("[scala.Short]")) {
            util.parsing.json.JSON.perThreadNumberParser = { input: String => input.toShort}
        } else if (paramTypeClient.endsWith("[scala.Double]")) {
            util.parsing.json.JSON.perThreadNumberParser = { input: String => input.toDouble}
        } else if (paramTypeClient.endsWith("[scala.Long]")) {
            util.parsing.json.JSON.perThreadNumberParser = { input: String => input.toLong}
        }

        val collection = util.parsing.json.JSON.parseFull(seqString).get.asInstanceOf[Seq[AnyVal]]

        collection
    }
}
