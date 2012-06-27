package cz.payola.common.rdf.ontology

/**
  * An ontology property of a class.
  * @param URI URI of the property.
  * @param typeURI URI of the property value type.
  */
class Property(val URI: String, val typeURI: Option[String])
