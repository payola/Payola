package controllers

import controllers.helpers._
import play.api.mvc._
import cz.payola.domain.entities.User
import play.api.mvc.AnyContentAsFormUrlEncoded
import s2js.runtime.shared.rpc.RpcException
import cz.payola.common.ValidationException
import java.lang.reflect.InvocationTargetException
import cz.payola.web.shared.Payola

/**
  * The only controller which handles requests from the client side. It receives a POST request with the following
  * contents:
  *
  * - method FQDN - eg. cz.payola.web.shared.RemoteObject.RemoteMethod - in the field named "method"
  * - map of parameters of the method - only simple types are allowed! - see further docs. to learn which types and
  * how this works
  *
  * All the requests are handled synchronously from the user's point of view. When one calls a remote method, it is
  * called on the server and the result is given back in the response. But from the client's point of view, one
  * distinguishes two types of request - synchronous and asynchronous.
  *
  * In both cases the result of the request is written into the body of the response. Those two types differs in
  * how they get processed on the client and/or server side:
  *
  * Synchrnous call would be called like
  *
  * val myResult = RemoteObj.remoteMethod(param1,param2,param3)
  *
  * Notice, that the thread which interprets the JS is blocked by waiting for the server to return the response. That's
  * why this technique is not very suitable for long-running tasks on the server-side. The whole UI gets frozen. One can
  * also see that it is pretty straightforward, you code as there is no client/server side of the application.
  *
  * In the case of asynchronous call, the two last parameters are callbacks. The first one, succesCallback is triggered
  * when the call was successful, the other one, failCallback, if there occured a problem, while the request was
  * dispatched.
  *
  * The typical usage would be
  *
  * RemoteObj.remoteMethod(param1, param2, successCallback (Any => Unit), failCallback (Throwable => Unit))
  *
  * The call of the remoteMethod returns immediately, so that interpreting of the JavaScript code below this line may
  * continue. When the call is done (response loaded), one of the callbacks is triggered. Keep this in mind while
  * using the mechanism. The typical use case would be loading a large graph structure from the server. One would
  * show a "loading" animation to the user to keep him noticed that something is happening on the background. After
  * the graph arrives to the client side of the application, the graph could be rendered using one of the visualization
  * plugins.
  *
  * The asynchronous version of the RPC call uses the Actors framework on the server side to guarantee the synchronous
  * processing of the call. That is useful in the situations when the remotely called method runs a new thread. One can
  * call the success/fail callback in the nested thread. Using the scala actors FW, it is guaranteed that this bahaviour
  * works well. In the case of the synchronous call, one has to handle this on his own - it is your choice, what and
  * when return something.
  *
  * While using the RPC, your objects from the cz.payola.web.common a cz.payola.web.shared are serialized using the
  * internal JsonSerializer to a JSON String. The serialized string is returned.
  *
  * You should never override your remote methods. The first override is always called to avoid paramTypes matching
  * (the params are given as strings, so if the number of parameters matches, we are not able to decide which one is
  * the right one for the call. If you do, feel free to set up a pull request :).
  */
object RPC extends PayolaController with Secured
{
    val exceptionSerializer = new ExceptionSerializer

    val jsonSerializer = new RPCSerializer

    val dispatcher = new RPCDispatcher(jsonSerializer)

    /**
      * Endpoint of the synchronous RPC call (mapped on /rpc)
      * @return
      */
    def index() = maybeAuthenticatedWithRequest { (user, request) => dispatchRequest(request, false, user)}

    /**
      * Endpoint of the asynchronous RPC call (maped on /rpc/async)
      * @return
      */
    def async() = maybeAuthenticatedWithRequest { (user, request) => dispatchRequest(request, true, user)}

    /**
      *
      * @param request
      * @param async
      * @return
      */
    def dispatchRequest(request: Request[AnyContent], async: Boolean, user: Option[User]) = {
        try {
            val params = parseParams(request)
            val response = dispatcher.dispatchRequest(params, async, user)
            Ok(response)
        } catch {
            case t => {
                Option(t.getCause).map(_.printStackTrace()).getOrElse(t.printStackTrace())
                raiseError(t)
            }
        }
    }

    /**
      * Only validation exceptions are allowed to pass to through the RPC to the client. All other exceptions are
      * wrapped int o a RpcException.
      * @param throwable
      * @return
      */
    def raiseError(throwable: Throwable) = {
        val exception = throwable match {
            case v: ValidationException => v
            case i: InvocationTargetException if i.getTargetException.isInstanceOf[ValidationException] => {
                i.getTargetException
            }
            case _: RpcException => throwable
            case _ => {
                var deepStackTrace = ""
                var cause = throwable
                while (cause != null) {
                    deepStackTrace += Option(cause.getMessage).map(_ + "\n\n" ).getOrElse("")
                    if (cause.getStackTrace.nonEmpty) {
                        deepStackTrace += cause.getStackTrace.toList.map(_.toString).mkString("\n") + "\n\n"
                    }
                    cause = cause.getCause
                }
                new RpcException("Uncaught exception during a remote method call.", deepStackTrace)
            }
        }

        InternalServerError(exceptionSerializer.serialize(exception))
    }

    /**
      *
      * @param request
      * @return
      */
    def parseParams(request: Request[AnyContent]) = {
        request.body match {
            case AnyContentAsFormUrlEncoded(data) => data
            case _ => Map.empty[String, Seq[String]]
        }
    }
}
