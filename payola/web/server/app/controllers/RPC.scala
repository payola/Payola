package controllers

import play.api.mvc._
import java.lang.reflect.Method
import cz.payola.scala2json.JSONSerializer
import cz.payola.scala2json.JSONSerializerOptions._

object RPC extends Controller
{
    def index() = Action {request =>
        val response = readRequestAndRespond(request)
        Ok(response)
    }

    def async() = Action {request =>
        val response = readRequestAndRespond(request, true)
        Ok(response)
    }

    private def readRequestAndRespond(request: Request[AnyContent], asynchronous: Boolean = false) : String = {
        val params = request.body match {
            case AnyContentAsFormUrlEncoded(data) => data
            case _ => Map.empty[String, Seq[String]]
        };

        params.toList.sortBy{_._2.head}
        val paramList = params.-("method").values

        val fqdn = params.get("method").getOrElse(null).head

        if (fqdn == null)
        {
            throw new Exception
        }

        val fqdnParts = fqdn.splitAt(fqdn.lastIndexOf("."))

        val result = asynchronous match {
            case true => invokeAsync(fqdnParts._1, fqdnParts._2.stripPrefix("."), paramList)
            case false => invoke(fqdnParts._1, fqdnParts._2.stripPrefix("."), paramList)
        }

        result
    }

    private def invokeAsync(objectName: String, methodName: String, params: Iterable[Seq[String]]) : String = {
        "TODO"
    }

    private def invoke(objectName: String, methodName: String, params: Iterable[Seq[String]]) : String = {
        val obj = Class.forName(objectName+"$");

        var methodToRun: Method = null

        obj.getMethods.foreach(method => {

          val currentMethodName = method.getName
          if (currentMethodName == methodName)
          {
              methodToRun = method
          }
        })

        if (methodToRun == null)
        {
            throw new Exception
        }

        val paramArray = new Array[java.lang.Object](params.size)
        val types = methodToRun.getParameterTypes

        var i = 0
        params.foreach(x => {
            paramArray.update(i, parseParam(x, types.apply(i)))
            i = i+1
        })


        val runnableObj = obj.getField("MODULE$").get(objectName)
        val result = methodToRun.invoke(runnableObj, paramArray:_*)

        val serializer = new JSONSerializer(result, JSONSerializerOptionDisableCustomSerialization)
        serializer.stringValue
    }

    private def parseParam(input: Seq[String], paramType: Class[_]) : java.lang.Object = {

        paramType.getName match {
            case "Boolean" => java.lang.Boolean.parseBoolean(input.head) : java.lang.Boolean
            case "java.lang.Boolean" => java.lang.Boolean.parseBoolean(input.head) : java.lang.Boolean
            case "boolean" => java.lang.Boolean.parseBoolean(input.head) : java.lang.Boolean
            case "Int" => java.lang.Integer.parseInt(input.head) : java.lang.Integer
            case "int" => java.lang.Integer.parseInt(input.head) : java.lang.Integer
            case "char" => input.head.charAt(0) : java.lang.Character
            case "Char" => input.head.charAt(0) : java.lang.Character
            case "java.lang.Character" => input.head.charAt(0) : java.lang.Character
            case "Double" => java.lang.Double.parseDouble(input.head) : java.lang.Double
            case "double" => java.lang.Double.parseDouble(input.head) : java.lang.Double
            case "java.lang.Double" => java.lang.Double.parseDouble(input.head) : java.lang.Double
            case "Float" => java.lang.Float.parseFloat(input.head) : java.lang.Float
            case "float" => java.lang.Float.parseFloat(input.head) : java.lang.Float
            case "java.lang.Float" => java.lang.Float.parseFloat(input.head) : java.lang.Float
            case "Array" =>  {
                input.map(item => {
                    parseParam(List(item), paramType.getTypeParameters.head.getClass)
                })
            }
            case _ => input.head.toString : java.lang.String
        }
    }
}
