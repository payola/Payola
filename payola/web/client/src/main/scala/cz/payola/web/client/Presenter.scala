package cz.payola.web.client

import s2js.adapters.js.browser.window
import s2js.runtime.shared.rpc.RpcException
import cz.payola.common.PayolaException
import cz.payola.web.client.views.bootstrap.modals.FatalErrorModal

trait Presenter
{
    def initialize()

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

    def delayed(delayInMilliseconds: Int)(f: () => Unit): Int = {
        window.setTimeout(f, delayInMilliseconds)
    }
}
