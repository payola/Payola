package cz.payola.web.client

import s2js.runtime.shared.rpc
import cz.payola.web.client.views.bootstrap.LoadingMessage
import cz.payola.web.client.views.bootstrap.modals.FatalErrorModal

trait Presenter
{
    def initialize()

    def fatalErrorHandler(error: Throwable) {
        // Unblock the page so the error description can be selected.
        unblockPage()

        val errorDescription = error match {
            case e: rpc.Exception => e.message + (if (e.deepStackTrace.nonEmpty) "\n\n" + e.deepStackTrace else "")
            case t => t.toString
        }
        val modal = new FatalErrorModal(errorDescription)
        modal.render()
    }

    def blockPageLoading(message: String) {
        blockPage(new LoadingMessage(message))
    }

    def blockPage(messageView: View) {
        View.blockPage(Some(messageView))
    }

    def unblockPage() {
        View.unblockPage()
    }
}
