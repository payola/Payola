package controllers

import play.api.mvc._

object RPC extends Controller
{
    def index = Action {
        Ok("{rsp: \"wow\"}")
    }
}
