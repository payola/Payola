package cz.payola.web.client.presenters.entity

import cz.payola.web.client.Presenter
import cz.payola.web.shared.managers.PrefixManager
import cz.payola.domain.entities.Prefix
import cz.payola.web.client.models.PrefixApplier

class PrefixPresenter extends Presenter
{
    val prefixApplier = new PrefixApplier()

    def initialize {
        // Gets properly ordered prefixes
        prefixApplier.prefixes = PrefixManager.getAvailablePrefixes()
    }

    /*
    def applyPrefix(uri: String): String = prefixApplier.applyPrefix(uri)

    def disapplyPrefix(uri: String): String = prefixApplier.disapplyPrefix(uri)
    */
}
