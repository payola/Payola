package cz.payola.common.entities

import cz.payola.common.Entity

/**
 * @author Ondřej Heřmánek (ondra.hermanek@gmail.com)
 */
trait Prefix extends Entity with OptionallyOwnedEntity with NamedEntity {
    protected var _prefix: String

    protected var _url: String

    /** Gets prefix for url */
    def prefix: String = _prefix

    /** Sets prefix for url */
    def prefix_=(value: String) {
        _prefix = value
    }

    /** Gets prefixed url */
    def url: String = _url

    /** Sets prefixed url */
    def url_=(value: String) {
        _url = value
    }

    /**
     * Applies prefix in given text
     * @param text Text that may contain prefixed uri to shorten
     * @return Returns text with prefixed uris
     */
    def applyPrefix(text:String): String = {
        replaceInUri(text, url, prefix + ":")
    }

    /**
     * Disapplies prefix in given text
     * @param text Text that may contain prefix
     * @return Returns text with full uris
     */
    def disapplyPrefix(text:String): String = {
        replaceInUri(text, prefix + ":", url)
    }

    private def replaceInUri(uri: String, lookFor: String, replaceWith: String): String = {
        // Unable to use replaceAll because s2js limitations
        var replacedUri = uri
        while (replacedUri.contains(lookFor))
        {
            replacedUri = replacedUri.replace(lookFor, replaceWith)
        }

        replacedUri
    }
}
