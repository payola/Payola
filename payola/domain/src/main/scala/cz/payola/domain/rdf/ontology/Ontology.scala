package cz.payola.domain.rdf.ontology

import java.io.StringReader
import com.hp.hpl.jena.ontology._

/** A class that represents an ontology model.
  */


object Ontology {

    def apply(ontologyModel: OntModel): Ontology = {
        val fact = new OntologyFactory(ontologyModel)
        fact.getOntology
    }

    def apply(ontologyString: String): Ontology = {
        val reader = new StringReader(ontologyString)

        // At this moment we don't allow any other format
        // as both OWL and RDFS should theoretically come
        // in XML format only
        val inputType = "RDF/XML"

        // Create a model and read it from the input string
        val jenaModel: OntModel = com.hp.hpl.jena.rdf.model.ModelFactory.createOntologyModel()
        jenaModel.read(reader, inputType)

        val m = apply(jenaModel)
        jenaModel.close()
        m
    }

    def empty: Ontology = new Ontology(Nil)
}

class Ontology(val classes: collection.Seq[Class]) extends cz.payola.common.entities.ontology.Ontology
{
    override def toString: String = {
        super.toString + " " + classes.toString
    }
}
