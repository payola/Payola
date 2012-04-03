package cz.payola.common.rdf

/**
  * An object identified with an URI.
  */
trait IdentifiedObject
{
    protected val _uri: String

    def uri = _uri

    override def toString: String = uri
}
