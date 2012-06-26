package cz.payola.domain.rdf.ontology

import com.hp.hpl.jena.ontology.OntProperty

object Property
{
    /**
      * Creates a new property from the specified [[com.hp.hpl.jena.ontology.OntProperty]].
      * @param ontologyProperty The Jena's property representation.
      * @return A new instance of the property class.
      */
    def apply(ontologyProperty: OntProperty): cz.payola.common.rdf.ontology.Property = {
        val propertyURI = ontologyProperty.getURI
        val typeURI = Option(ontologyProperty.getRDFType).map(_.getURI)
        new cz.payola.common.rdf.ontology.Property(propertyURI, typeURI)
    }
}
