package cz.payola.web.client.presenters.entity

import cz.payola.web.client.Presenter
import cz.payola.web.shared.managers.PrefixManager
import cz.payola.domain.entities.Prefix

class PrefixPresenter extends Presenter
{
    private var prefixes: Seq[Prefix] = null;

    def initialize {
        // Gets properly ordered prefixes
        prefixes = PrefixManager.getAvailablePrefixes()
    }

    def applyPrefix(uri: String): String = {
        val p = prefixes.flatMap(_.applyPrefix(uri))
        p.headOption.getOrElse(uri)
    }

    def disapplyPrefix(uri: String): String = {
        prefixes.flatMap(_.disapplyPrefix(uri)).headOption.getOrElse(uri)
    }
}
