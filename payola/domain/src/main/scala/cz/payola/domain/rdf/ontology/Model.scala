package cz.payola.domain.rdf.ontology

import java.io.StringReader
import com.hp.hpl.jena.ontology._

/** A class that represents an ontology model.
  */


object Model {

    def apply(ontologyModel: OntModel): Model = {
        val fact = new ModelFactory(ontologyModel)
        fact.getModel
    }

    def apply(ontologyString: String): Model = {
        val reader = new StringReader(ontologyString)

        val inputType = "RDF/XML"

        // Create a model and read it from the input string
        val jenaModel: OntModel = com.hp.hpl.jena.rdf.model.ModelFactory.createOntologyModel()
        jenaModel.read(reader, "RDF/XML")

        val m = apply(jenaModel)
        jenaModel.close()
        m
    }

    def empty: Model = new Model(Nil)
}

class Model(val classes: collection.Seq[Class])
{
    override def toString: String = {
        super.toString + " " + classes.toString
    }
}
