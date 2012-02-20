package controllers

import play.api.mvc._

object RPC extends Controller
{
    def index() = Action {request =>
        val params = request.body match {
            case AnyContentAsFormUrlEncoded(data) => data
            case _ => Map.empty[String, Seq[String]]
        };
        val serializedParams = params.foldLeft("") {(buf, par) =>
            buf + "%s: [%s], ".format(par._1, par._2.mkString(", "))
        }
        Ok("Response for received request: %s\n\n and params: %s".format(request.body.toString, serializedParams))
    }
}
