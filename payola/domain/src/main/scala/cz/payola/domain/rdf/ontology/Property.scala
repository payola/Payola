package cz.payola.domain.rdf.ontology

/** A class that represents an ontology property.
  *
  * @param URI Property URI.
  */
class Property(val URI: String, val typeURI: Option[String]) extends cz.payola.common.entities.ontology.Property
{
    /** Overriding toString method to include URI in the description.
      *
      * @return Object description.
      */
    override def toString = super.toString + " => " + URI
}
