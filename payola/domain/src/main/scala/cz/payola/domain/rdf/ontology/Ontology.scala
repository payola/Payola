package cz.payola.domain.rdf.ontology

import scala.collection._
import scala.io.Source
import java.io._
import cz.payola.common.rdf.ontology._
import scala.Predef.Map
import scala.Seq

/**
  * An ontology factory with several methods for ontology creation.
  */
object Ontology
{
    /**
      * Reads a new ontology from an input stream.
      * @param inputStream Input Stream.
      * @param encoding Encoding of the input stream. UTF-8 by default.
      * @return A new instance of the ontology class.
      */
    def apply(inputStream: InputStream, encoding: String = "UTF-8"): Ontology = {
        val xml: String = Source.fromInputStream(inputStream, encoding).mkString
        Ontology(xml)
    }

    /**
      * Creates a new ontology from a string which contains a XML document with either RDFS or OWL ontology.
      * @param ontologyString XML document.
      * @return A new instance of the ontology class.
      */
    def apply(ontologyString: String): Ontology = {
        val reader = new StringReader(ontologyString)

        // At this moment we don't allow any other format/ as both OWL and RDFS should theoretically come in XML
        // format only.
        val inputType = "RDF/XML"

        // Create a model and read it from the input string.
        val jenaModel = com.hp.hpl.jena.rdf.model.ModelFactory.createOntologyModel()
        jenaModel.read(reader, inputType)

        val ontology = Ontology(jenaModel)
        jenaModel.close()
        ontology
    }

    /**
      * Creates a new ontology from the specified [[com.hp.hpl.jena.ontology.OntModel]].
      * @param ontologyModel Jena's ontology representation.
      * @return A new instance of the ontology class.
      */
    def apply(ontologyModel: com.hp.hpl.jena.ontology.OntModel): Ontology = {
        val classes = mutable.HashMap.empty[String, Class]
        val superClasses = mutable.HashMap.empty[String, mutable.Buffer[String]]

        val classIterator = ontologyModel.listClasses()
        while (classIterator.hasNext) {
            val ontologyClass = classIterator.next()
            val c = Class(ontologyClass)
            classes.put(c.URI, c)

            val superClassIterator = ontologyClass.listSuperClasses()
            val classSuperClasses = superClasses.getOrElseUpdate(c.URI, mutable.Buffer.empty[String])
            while (superClassIterator.hasNext) {
                classSuperClasses += superClassIterator.next().getURI
            }
        }

        new Ontology(classes.toMap, superClasses.mapValues(_.toSeq).toMap)
    }

    /**
      * Returns an empty ontology.
      */
    def empty: Ontology = new Ontology(Map.empty[String, Class], Map.empty[String, Seq[String]])
}
