package cz.payola.web.client.models

import cz.payola.common.entities.Prefix
import cz.payola.web.client.presenters.entity.PrefixPresenter
import s2js.compiler.javascript

/**
 * @author Ondřej Heřmánek (ondra.hermanek@gmail.com)
 */
class PrefixApplier(prefixPresenter: PrefixPresenter = null)
{
    var prefixes: Seq[Prefix] = Nil

    def applyPrefix(uri: String): String = {
        if (prefixes != Nil)
        {
            prefixes.flatMap(_.applyPrefix(uri)).headOption.getOrElse(uri)
        }
        else
        {
            uri
        }
    }

    def disapplyPrefix(uri: String): String = {
        var result = uri

        if (prefixes != Nil)
        {
            result = prefixes.flatMap(_.disapplyPrefix(uri)).headOption.getOrElse(uri)
        }

        // Prefix not find, try check for unknown
        if (result == uri)
        {
            if (prefixPresenter != null)
            {
                prefixPresenter.findUnknownPrefix(uri)
                { p => showDialog(result, Some(p))  }
                { e => showDialog(result, None) }
            }
        }

        result
    }

    @javascript(""" $.growlUI(title, message, 5000) """)
    private def showBanner(title: String, message: String) {}

    private def showDialog(prefixedUri: String, uri: Option[String]) {
        val title = uri.map(u => "New prefix").getOrElse("Unknown prefix")

        val message = uri.map(
            ("You have used an unknown prefix in uri '%s', " +
            "it has been associated to '%s' url and added to global prefixes.").format(prefixedUri, _)
        ).getOrElse(
            ("You have used an unknown prefix in uri '%s', " +
            "which has not been mapped to any url.").format(prefixedUri)
        )

        showBanner(title, message)
    }
}