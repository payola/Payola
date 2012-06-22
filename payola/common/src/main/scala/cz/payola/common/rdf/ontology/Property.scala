package cz.payola.common.rdf.ontology

trait Property
{
    val URI: String

    val typeURI: Option[String]
}

/* TODO

/**
  * An ontology property of a class.
  */
trait Property
{
    /** URI of the property. */
    val URI: String

    /** URI of the property value type. */
    val typeURI: Option[String]
}


*/
