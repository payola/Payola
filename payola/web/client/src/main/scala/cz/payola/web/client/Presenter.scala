package cz.payola.web.client

import s2js.adapters.browser._
import s2js.runtime.shared.rpc.RpcException
import cz.payola.web.client.views.bootstrap.modals.FatalErrorModal
import cz.payola.common.PayolaException

trait Presenter
{
    /**
     * This method should contain code needed to initialize the presenter, moreover, it should be called after
     * creation of a new Presenter instance.
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

    def blockPage(message: String = "") {
        View.blockPage(message)
    }

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
