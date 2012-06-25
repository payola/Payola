package cz.payola.common.rdf.ontology

trait Ontology
{
    val classes: collection.Seq[Class]
}

/* TODO

/**
  * A description of a set of object classes and relationships between them (http://www.w3.org/TR/owl-features/).
  */
trait Ontology
{
    /** Type of the classes the ontology contains. */
    type ClassType <: Class

    /** The classes in the ontology indexed by their URIs. */
    val classes: immutable.Map[String, ClassType]
}


*/
