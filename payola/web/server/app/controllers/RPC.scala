package controllers

import play.api.mvc._
import cz.payola.web.shared._
import java.lang.reflect.Method
import collection.mutable.ListBuffer

object RPC extends Controller
{
    def index() = Action {request =>

        val params = request.body match {
            case AnyContentAsFormUrlEncoded(data) => data
            case _ => Map.empty[String, Seq[String]]
        };

        params.toList.sortBy{_._2.head}
        val paramList = params.values

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

        val runnableObj = obj.getField("MODULE$").get(objectName)
        methodToRun.invoke(runnableObj, Array(new Integer(2), "foo"):_*).toString
    }
}
