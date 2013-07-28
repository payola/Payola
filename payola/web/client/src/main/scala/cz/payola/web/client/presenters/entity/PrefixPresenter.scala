package cz.payola.web.client.presenters.entity

import cz.payola.web.client.Presenter
import cz.payola.web.shared.managers.PrefixManager
import cz.payola.domain.entities.Prefix
import cz.payola.web.client.models.PrefixApplier

class PrefixPresenter extends Presenter
{
    val prefixApplier = new PrefixApplier(this)
    val falseHitPrefixes = List("http")

    def initialize {
        // Gets properly ordered prefixes
        prefixApplier.prefixes = PrefixManager.getAvailablePrefixes()
    }

    def findUnknownPrefix(prefixedUrl: String)(successCallback: String => Unit)(errorCallback: Throwable => Unit) {
        val delim = prefixedUrl.indexOf(":")

        if (delim > 0)
        {
            val prefix = prefixedUrl.substring(0, delim)

            // Eliminate false-hit prefixes
            if (!falseHitPrefixes.find(prefix.startsWith(_)).isDefined)
            {
                val newSuccessCallback = {
                    p: String => {
                        // Update prefixes to contain the new one
                        prefixApplier.prefixes = PrefixManager.getAvailablePrefixes()
                        successCallback(p)
                    }
                }

                PrefixManager.findUnknownPrefix(prefix)(newSuccessCallback)(errorCallback)
            }
        }
    }
}
