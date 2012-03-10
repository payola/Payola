package cz.payola.common.rdf

/**
  * An object identified with an URI.
  */
trait IdentifiedObject
{
    val uri: String

    override def toString: String = uri
}
