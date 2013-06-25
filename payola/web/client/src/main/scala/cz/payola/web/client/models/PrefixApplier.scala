package cz.payola.web.client.models

import cz.payola.common.entities.Prefix
import cz.payola.web.client.presenters.entity.PrefixPresenter
import cz.payola.web.client.presenters.components.PrefixDialog

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
                { p => showDialog(result, Some(p)) }
                { e => showDialog(result, None) }
            }
        }

        result
    }

    private def showDialog(prefixedUri: String, uri: Option[String])
    {
        new PrefixDialog(prefixedUri, uri).render(s2js.adapters.browser.document.body)
    }
}