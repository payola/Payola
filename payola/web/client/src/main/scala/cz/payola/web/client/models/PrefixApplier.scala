package cz.payola.web.client.models

import cz.payola.common.entities.Prefix
import cz.payola.web.client.presenters.entity.PrefixPresenter
import s2js.compiler.javascript
import scala.actors.Actor
import scala.collection.mutable

/**
 * @author Ondřej Heřmánek (ondra.hermanek@gmail.com)
 */
class PrefixApplier(prefixPresenter: PrefixPresenter = null)
{
    var prefixes: Seq[Prefix] = Nil

    /**
     * Tries to apply all prefixes on the text
     * @param text Text with uris to be shortened
     * @return Text with all possible uris shortened
     */
    def applyPrefix(text: String): String = {
        if (prefixes != Nil) {
            var appliedText = text
            prefixes.foreach(p => appliedText = p.applyPrefix(appliedText))
            appliedText
        }
        else {
            text
        }
    }

    /**
     * Disapplies as much prefixes as possible from text
     * @param text Text with shortened uris to be replaced back
     * @param multiplePrefixes Specifies whether text can contain multiple prefixes
     * @return Returns text with as much full uris as possible
     */
    def disapplyPrefix(text: String, multiplePrefixes: Boolean = false): String = {
        if (multiplePrefixes) {
            disapplyMultiplePrefixes(text)
        }
        else {
            disapplySinglePrefix(text)
        }
    }

    private def disapplyMultiplePrefixes(text: String): String = {
        val separators = new mutable.ArrayBuffer[Char]()
        separators.append(' ', '\n')

        // For every word in text disapply uri separately and return back to the text
        var disappliedText = text
        text.split(separators.toArray).foreach{uri =>
            val result = disapplySinglePrefix(uri)
            if (result != uri) {
                disappliedText = disappliedText.replace(uri, result)
            }
        }

        disappliedText
    }

    private def disapplySinglePrefix(text: String): String = {
        if (prefixes != Nil) {
            var disappliedText = text
            val result = prefixes.find(p => (disappliedText = p.disapplyPrefix(text)) != text).headOption.getOrElse(text)

            if (result == text)
            {
                findUnknownPrefix(text)
            }

            text
        }
        else {
            findUnknownPrefix(text)
            text
        }
    }

    private def findUnknownPrefix(uri: String) {
        if (prefixPresenter != null)
        {
            prefixPresenter.findUnknownPrefix(uri)
            { p => showDialog(uri, Some(p))  }
            { e => showDialog(uri, None) }
        }
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