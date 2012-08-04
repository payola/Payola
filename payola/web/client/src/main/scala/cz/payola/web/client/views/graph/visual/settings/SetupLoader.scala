package cz.payola.web.client.views.graph.visual.settings

import s2js.adapters.browser._

class SetupLoader
{
    private def setItem(where: String, what: String, reset: Boolean) {
        if (reset || getItem(where).isEmpty) {
            window.localStorage.setItem(where, what)
        }
    }

    private def getItem(where: String): Option[String] = {
        val gotFromMemory = window.localStorage.getItem(where)
        if (gotFromMemory == null) {
            None
        } else {
            Some(gotFromMemory)
        }
    }

    def getValue(localStorageKey: String): Option[String] = {
        val value = getItem(localStorageKey)
        if (value.isEmpty) {
            None
        } else {
            value
        }
    }
}
