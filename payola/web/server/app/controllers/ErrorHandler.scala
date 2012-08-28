package controllers

import play.api.mvc._

object ErrorHandler extends Controller
{
    def error() = Action {
        InternalServerError(views.html.error("An error occured."))
    }
    def notFound() = Action {
        InternalServerError(views.html.error("Not found."))
    }
}
