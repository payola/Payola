package cz.payola.common.rdf.ontology

import scala.collection.immutable

/**
  * An ontology class of objects.
  * @param uri URI of the class.
  * @param label Label of the class.
  * @param comment Comment of the class.
  * @param properties Properties of the class indexed by their URIs.
  */
class Class(val uri: String, val label: String, val comment: String, val properties: immutable.Map[String, Property])
{
    /**
      * Merges this class with the other one.
      * @param otherClass The class to merge with this class.
      * @return A new merged class.
      */
    def +(otherClass: Class): Class = {
        new Class(uri, label, comment, properties ++ otherClass.properties)
    }

    override def toString: String = {
        """%s {\n\t
            %s (%s)\n\t
            URI: %s\n\t"
            Properties: %s\n
         }""".format(super.toString, label, comment, uri, properties.toString())
    }
}
