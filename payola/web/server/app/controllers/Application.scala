package controllers

import play.api.mvc._

object Application extends Controller
{
    def index = Action {
        Ok(views.html.index())
    }
    def rpcTest = Action {
        Ok(views.html.test())
    }
}
