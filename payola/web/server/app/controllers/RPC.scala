package controllers

import helpers.{RPCActionMessage, RPCReply, RPCActionExecutor}
import play.api.mvc._
import java.lang.reflect.Method
import cz.payola.scala2json.JSONSerializer
import cz.payola.scala2json.classes.SimpleSerializationClass
import cz.payola.scala2json.rules.BasicSerializationRule
import cz.payola.domain.rdf.{RDFEdge, RDFGraph, RDFIdentifiedNode, RDFLiteralNode}

/**
  * The only controller which handles requests from the client side. It receives a POST request with the following
  * contents:
  *
  * - method FQDN - eg. cz.payola.web.shared.RemoteObject.RemoteMethod - in the field named "method"
  * - map of parameters of the method - only simple types are allowed! - see further docs. to learn which types and
  * how this works
  *
  * All the requests are handled synchronously from the user's point of view. When one calls a remote method, it is
  * called on the server and the result is given back in the response. But from the client's point of view, one
  * distinguishes two types of request - synchronous and asynchronous.
  *
  * In both cases the result of the request is written into the body of the response. Those two types differs in
  * how they get processed on the client and/or server side:
  *
  * Synchrnous call would be called like
  *
  * val myResult = RemoteObj.remoteMethod(param1,param2,param3)
  *
  * Notice, that the thread which interprets the JS is blocked by waiting for the server to return the response. That's
  * why this technique is not very suitable for long-running tasks on the server-side. The whole UI gets frozen. One can
  * also see that it is pretty straightforward, you code as there is no client/server side of the application.
  *
  * In the case of asynchronous call, the two last parameters are callbacks. The first one, succesCallback is triggered
  * when the call was successful, the other one, failCallback, if there occured a problem, while the request was
  * dispatched.
  *
  * The typical usage would be
  *
  * RemoteObj.remoteMethod(param1, param2, successCallback (Any => Unit), failCallback (Throwable => Unit))
  *
  * The call of the remoteMethod returns immediately, so that interpreting of the JavaScript code below this line may
  * continue. When the call is done (response loaded), one of the callbacks is triggered. Keep this in mind while
  * using the mechanism. The typical use case would be loading a large graph structure from the server. One would
  * show a "loading" animation to the user to keep him noticed that something is happening on the background. After
  * the graph arrives to the client side of the application, the graph could be rendered using one of the visualisation
  * plugins.
  *
  * The asynchronous version of the RPC call uses the Actors framework on the server side to guarantee the synchronous
  * processing of the call. That is useful in the situations when the remotely called method runs a new thread. One can
  * call the success/fail callback in the nested thread. Using the scala actors FW, it is guaranteed that this bahaviour
  * works well. In the case of the synchronous call, one has to handle this on his own - it is your choice, what and
  * when return something.
  *
  * While using the RPC, your objects from the cz.payola.web.common a cz.payola.web.shared are serialized using the
  * internal JsonSerializer to a JSON String. The serialized string is returned.
  *
  * You should never override your remote methods. The first override is always called to avoid paramTypes matching
  * (the params are given as strings, so if the number of parameters matches, we are not able to decide which one is
  * the right one for the call. If you do, feel free to set up a pull request :).
  */
object RPC extends Controller
{
    val jsonSerializer = new JSONSerializer()
    
    /** Graph rules **/
    _loadGraphSerializationRules
    
    def _loadGraphSerializationRules = {
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

    /**
      * Endpoint of the synchronous RPC call (mapped on /rpc)
      * @return
      */
    def index() = Action {request =>
        val response = readRequestAndRespond(request)
        Ok(response)
    }

    /**
      * Endpoint of the asynchronous RPC call (maped on /rpc/async)
      * @return
      */
    def async() = Action {request =>
        val response = readRequestAndRespond(request, true)
        Ok(response)
    }

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
    private def readRequestAndRespond(request: Request[AnyContent], asynchronous: Boolean = false): String = {
        // initialize the params object with the data from the given request
        val params = request.body match {
            case AnyContentAsFormUrlEncoded(data) => data
            case _ => Map.empty[String, Seq[String]]
        };

        // get the name of the method
        // when empty, throw an exception
        val fqdn = params.get("method").getOrElse(null).head

        if (fqdn == null) {
            throw new Exception
        }

        // sort the params list with the parameter name (presumably a ordering-index - 1,2,3,...)
        params.toList.sortBy {
            _._2.head
        }
        val paramTypesJson = params.getOrElse("paramTypes",List("[]")).head
        val paramTypes = util.parsing.json.JSON.parseFull(paramTypesJson)

        // the map keys are now irellevant, continue with values only
        val paramList = params.-("method").-("paramTypes").values

        // split the method names with "." to get package name and the name of the method without package
        // beware of the leading dot
        val fqdnParts = fqdn.splitAt(fqdn.lastIndexOf("."))

        // call the remote method synchronously or asynchronously - depends on the asynchronous parameter
        val result = asynchronous match {
            case true => invokeAsync(fqdnParts._1, fqdnParts._2.stripPrefix("."), paramList, paramTypes.get.asInstanceOf[Seq[String]])
            case false => invoke(fqdnParts._1, fqdnParts._2.stripPrefix("."), paramList, paramTypes.get.asInstanceOf[Seq[String]])
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
    private def invokeAsync(objectName: String, methodName: String, params: Iterable[Seq[String]], paramTypes: Seq[String]): String = {
        // while objects are not really a Java thing, they are compiled into static classes named with trailing $ sign
        val obj = Class.forName(objectName + "$"); // TODO: use Scala reflection when released

        // get the desired method on the object
        val methodOption = obj.getMethods.find(_.getName == methodName)
        // if the method is not defined, throw an Exception
        if (!methodOption.isDefined) {
            throw new Exception
        }

        // "objectify" the desired mezhod to be able to invoke it later
        // this is a very ugly Java stuff, but Scala is not able to do this at the time
        val methodToRun: java.lang.reflect.Method = methodOption.getOrElse(null)
        val runnableObj = obj.getField("MODULE$").get(objectName)

        // allocate an array of objects for the parameters (adding 2 for callbacks)
        val paramArray = new Array[java.lang.Object](params.size + 2)

        // list parameter types to deserialize the passed values
        val types = methodToRun.getParameterTypes

        // update each parameter and replace it with its properly typed representation
        var i = 0
        params.foreach(x => {
            paramArray.update(i, parseParam(x, types.apply(i), paramTypes.apply(i)))
            i = i + 1
        })

        // create and start the actor
        val executor = new RPCActionExecutor()
        executor.start()

        // add param - success callback
        paramArray.update(i, {x: Any => executor ! new RPCReply(x)})
        i = i + 1
        // add param - fail callback - takes only Throwable as a parameter
        paramArray.update(i, {x: Throwable => executor ! new RPCReply(x)})
        i = i + 1

        // invoke the remote method (!? for synchronous behaviour)
        val result = executor !? RPCActionMessage(methodToRun, runnableObj, paramArray)

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
    private def invoke(objectName: String, methodName: String, params: Iterable[Seq[String]], paramTypes: Seq[String]): String = {
        // while objects are not really a Java thing, they are compiled into static classes named with trailing $ sign
        val obj = Class.forName(objectName + "$");

        // get the desired method on the object
        val methodOption = obj.getMethods.find(_.getName == methodName)
        // if the method is not defined, throw an Exception
        if (!methodOption.isDefined) {
            throw new Exception
        }

        // "objectify" the desired mezhod to be able to invoke it later
        // this is a very ugly Java stuff, but Scala is not able to do this at the time
        val methodToRun: java.lang.reflect.Method = methodOption.getOrElse(null)
        val runnableObj = obj.getField("MODULE$").get(objectName)

        // update each parameter and replace it with its properly typed representation
        val paramArray = new Array[java.lang.Object](params.size)
        val types = methodToRun.getParameterTypes

        // update each parameter and replace it with its properly typed representation
        var i = 0
        params.foreach(x => {
            paramArray.update(i, parseParam(x, types.apply(i), paramTypes.apply(i)))
            i = i + 1
        })

        // invoke the remote method (!? for synchronous behaviour)
        val result = methodToRun.invoke(runnableObj, paramArray: _*)

        val isString = result.isInstanceOf[String];

        val serialized = jsonSerializer.serialize(result)
        serialized
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

        if (paramType.getName.startsWith("scala.collection"))
        {
            parseSequence(input, paramType, paramTypeClient)
        }else{
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

    private def parseSequence(input: Seq[String], paramType: Class[_], paramTypeClient: String) : java.lang.Object = {
        val seqString = input.head

        if (paramTypeClient.endsWith("[scala.Int]"))
        {
            util.parsing.json.JSON.perThreadNumberParser = {input : String => input.toInt}
        }else if (paramTypeClient.endsWith("[scala.Float]"))
        {
            util.parsing.json.JSON.perThreadNumberParser = {input : String => input.toFloat}
        }else if (paramTypeClient.endsWith("[scala.Short]"))
        {
            util.parsing.json.JSON.perThreadNumberParser = {input : String => input.toShort}
        }else if (paramTypeClient.endsWith("[scala.Double]"))
        {
            util.parsing.json.JSON.perThreadNumberParser = {input : String => input.toDouble}
        }

        val collection = util.parsing.json.JSON.parseFull(seqString).get.asInstanceOf[Seq[AnyVal]]

        collection
    }
}
