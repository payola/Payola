package cz.payola.web.client

import s2js.adapters.browser._
import s2js.runtime.shared.rpc.RpcException
import cz.payola.web.client.views.bootstrap.modals.FatalErrorModal
import cz.payola.common.PayolaException

/**
 * The base presenter trait. All the presenters should be derived from this trait. It defines the initialize method
 * which is meant to be called after the presenter is created. It also defines fatal error handler behaviour, methods
 * for blocking and unblocking the UI of the page. There is also syntactic sugar method delayed which is a wrapper for
 * window.setTimeout call.
 */
trait Presenter
{
    /**
     * This method should contain code needed to initialize the presenter, moreover, it should be called after
     * creation of a new Presenter instance. This method is not called automatically, you need to call it by
     * yourself!
     */
    def initialize()

    /**
     * Displays a Modal dialog which cannot be closed. The dialog contains an error message, which is taken from the
     * passed Throwable parameter and a stacktrace, if available.
     * @param error Cause of the fatal error.
     */
    def fatalErrorHandler(error: Throwable) {
        // Unblock the page so the error description can be selected.
        unblockPage()

        val errorDescription = error match {
            case e: RpcException => e.message + (if (e.deepStackTrace.nonEmpty) "\n\n" + e.deepStackTrace else "")
            case e: PayolaException => e.message
            case t => t.toString
        }
        val modal = new FatalErrorModal(errorDescription)
        modal.render()
    }

    /**
     * Blocks the whole page.
     * @param message Message displayed on page while being blocked.
     */
    def blockPage(message: String = "") {
        View.blockPage(message)
    }

    /**
     * Unblocks the UI.
     */
    def unblockPage() {
        View.unblockPage()
    }

    /**
     * Sets a timeout and performs the f function after the delayInMilliseconds runs out.
     * @param delayInMilliseconds how long to wait before function is performed
     * @param f run this function after the timeout
     * @return number of the javascript timeout function
     */
    def delayed(delayInMilliseconds: Int)(f: () => Unit): Int = {
        window.setTimeout(f, delayInMilliseconds)
    }
}
