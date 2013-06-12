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
     * Applies prefix if prefix shortens uri
     * @param uri Uri to shorten
     * @return Returns None if prefix doesn't shorten uri, Some(shorted uri) otherwise
     */
    def applyPrefix(uri:String): Option[String] = {
        replaceInUri(uri, url, prefix + ":")
    }

    def disapplyPrefix(uri:String): Option[String] = {
        replaceInUri(uri, prefix + ":", url)
    }

    private def replaceInUri(uri: String, lookFor: String, replaceWith: String): Option[String] = {
        if (uri.contains(lookFor))
        {
            // No replaceAll method in client javascript
            var replacedUri = uri
            while (replacedUri.contains(lookFor))
            {
                replacedUri = replacedUri.replace(lookFor, replaceWith)
            }

            Some(replacedUri)
        }
        else
            None
    }
}
