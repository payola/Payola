package controllers

import play.api.mvc._
import s2js.runtime.shared.DependencyProvider

object Application extends Controller
{
    def index = Action {
        Ok(views.html.index())
    }
    def rpcTest = Action {
        Ok(views.html.test())
    }
    def javaScriptPackage(symbol: String) = Action {
        val javaScript = DependencyProvider.get(List("bootstrap", symbol), Nil).javaScript
        Ok(javaScript)
    }
}
