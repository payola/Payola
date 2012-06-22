package cz.payola.common.rdf.ontology

trait Class
{
    val URI: String

    val label: String

    val comment: String

    def properties: collection.Seq[Property]

    def superclasses: collection.Seq[Class]
}

/* TODO

package cz.payola.common.rdf.ontology

import scala.collection.immutable

/**
  * An ontology class of objects.
  */
trait Class
{
    /** Type of the class property. */
    type PropertyType <: Property

    /** URI of the class. */
    val URI: String

    /** Label of the class. */
    val label: String

    /** Comment of the class. */
    val comment: String

    /** Properties of the class indexed by their URIs. */
    val properties: immutable.Map[String, PropertyType]

    /** Super classes of the class indexed by their URIs. */
    val superClasses: immutable.Map[String, this.type]
}


 */
