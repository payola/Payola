package cz.payola.web.client

import cz.payola.web.client.views.bootstrap.LoadingMessage

trait Presenter
{
    def initialize()

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
