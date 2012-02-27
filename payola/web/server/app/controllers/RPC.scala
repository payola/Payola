package controllers

import play.api.mvc._
import cz.payola.web.shared._
import java.lang.reflect.Method
import collection.mutable.ListBuffer
import cz.payola.scala2json.JSONSerializer

object RPC extends Controller
{
    def index() = Action {request =>

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
        Ok(invoke(fqdnParts._1, fqdnParts._2.stripPrefix("."), paramList))
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

        val serializer = new JSONSerializer(result)
        serializer.stringValue
    }

    private def parseParam(input: Seq[String], paramType: Class[_]) : java.lang.Object = {

        paramType.getName match {
            case "Boolean" => java.lang.Boolean.parseBoolean(input.head) : java.lang.Boolean
            case "java.lang.Boolean" => java.lang.Boolean.parseBoolean(input.head) : java.lang.Boolean
            case "boolean" => java.lang.Boolean.parseBoolean(input.head) : java.lang.Boolean
            case "Int" => java.lang.Integer.parseInt(input.head) : java.lang.Integer
            case _ => input.head.toString : java.lang.String
        }
    }
}
