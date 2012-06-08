package cz.payola.domain.rdf.ontology

import com.hp.hpl.jena.ontology._
import java.io._
import scala.io.Source

/** A class that represents an ontology model and its companion object
  * with several apply methods for ontology creation.
  *
  */


object Ontology {

    /** Reads an Ontology from input stream.
      *
      * @param is Input Stream.
      * @param encoding Encoding of the input stream. UTF-8 by default.
      * @return Instance of ontology.
      */
    def apply(is: InputStream, encoding: String = "UTF-8"): Ontology = {
        val xml: String = Source.fromInputStream(is, encoding).mkString
        apply(xml)
    }

    /** Creates a new ontology from Jena's OntModel.
      *
      * @param ontologyModel Jena's ontology representation.
      * @return New Ontology model.
      */
    def apply(ontologyModel: OntModel): Ontology = {
        // Just pass it to the ontology factory
        val fact = new OntologyFactory(ontologyModel)
        fact.getOntology
    }

    /** Creates a new ontology from a string which contains a XML document
      * with either RDFS or OWL ontology.
      *
      * @param ontologyString XML document.
      * @return New Ontology model.
      */
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

    /** Returns an empty ontology.
      *
      * @return Empty ontology.
      */
    def empty: Ontology = new Ontology(Nil)

    /** Merges two ontologies into a new one. Equivalent to o1 + o2.
      *
      * @param o1 First ontology.
      * @param o2 Second ontology.
      * @return New instance with merged classes and properties.
      */
    def merge(o1: Ontology, o2: Ontology): Ontology = {
        OntologyMerger(o1, o2)
    }
}

class Ontology(val classes: collection.Seq[Class]) extends cz.payola.common.entities.ontology.Ontology
{

    /** Merges this onotlogy with the other one into a new one.
      *
      * @param otherOntology The other ontology.
      * @return New instance with merged classes and properties.
      */
    def +(otherOntology: Ontology): Ontology = {
        Ontology.merge(this, otherOntology)
    }

    def containsClassWithURI(uri: String): Boolean = {
        getClassForURI(uri).isDefined
    }

    def getClassForURI(uri: String): Option[Class] = {
        classes.find { p: Class => p.URI == uri }
    }

    /** Overriding toString - printing the class list.
      *
      * @return Object description.
      */
    override def toString: String = {
        super.toString + " " + classes.toString
    }
}
