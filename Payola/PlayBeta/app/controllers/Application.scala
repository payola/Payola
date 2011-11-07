package controllers

import play.api._
import play.api.mvc._

import payola.helloworld._

object Application extends Controller {
    def index = Action {
        Ok(views.html.index("Payola rulezz: " + HelloWorld.hello))
    }
}