package cz.payola.web.client.presenters.entity

import cz.payola.web.client.Presenter
import cz.payola.web.shared.managers.PrefixManager
import cz.payola.domain.entities.Prefix
import cz.payola.web.client.models.PrefixApplier

class PrefixPresenter extends Presenter
{
    val prefixApplier = new PrefixApplier(this)

    def initialize {
        // Gets properly ordered prefixes
        prefixApplier.prefixes = PrefixManager.getAvailablePrefixes()
    }

    def findUnknownPrefix(prefixedUrl: String)(successCallback: String => Unit)(errorCallback: Throwable => Unit) {
        val delim = prefixedUrl.indexOf(":")

        if (delim > 0)
        {
            PrefixManager.findUnknownPrefix(prefixedUrl.substring(0, delim))(successCallback)(errorCallback)
        }
    }
}
