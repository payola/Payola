package cz.payola.domain.rdf.ontology

import scala.collection.mutable
import cz.payola.common.rdf.ontology._

object Class
{
    /**
      * Creates a new class from the specified [[com.hp.hpl.jena.ontology.OntClass]].
      * @param ontologyClass The Jena's class representation.
      * @return A new instance of the class.
      */
    def apply(ontologyClass: com.hp.hpl.jena.ontology.OntClass): Class = {
        val classURI = ontologyClass.getURI
        val properties = mutable.HashMap.empty[String, Property]

        val propertyIterator = ontologyClass.listDeclaredProperties()
        while (propertyIterator.hasNext) {
            val property = Property(propertyIterator.next())
            properties.put(property.URI, property)
        }

        new Class(classURI, ontologyClass.getLabel(null), ontologyClass.getComment(null), properties.toMap)
    }
}
