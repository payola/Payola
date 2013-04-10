package cz.payola.common.entities

import cz.payola.common.Entity

/**
 * @author Ondřej Heřmánek (ondra.hermanek@gmail.com)
 */
trait Prefix extends Entity with ShareableEntity with OptionallyOwnedEntity with NamedEntity {
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
        _url = value;
    }
}
