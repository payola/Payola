import controllers.routes
import play.api.GlobalSettings
import play.api.mvc._

object Global extends GlobalSettings {
    /*override def onError(request : play.api.mvc.RequestHeader, ex : scala.Throwable) : play.api.mvc.Result = {
        play.api.mvc.Results.Redirect(routes.ErrorHandler.error())
    }
    override def onHandlerNotFound(request: RequestHeader): Result = {
        play.api.mvc.Results.Redirect(routes.ErrorHandler.notFound())
    } */
}
