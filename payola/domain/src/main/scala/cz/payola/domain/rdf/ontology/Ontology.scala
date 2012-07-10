package cz.payola.domain.rdf.ontology

import scala.collection._
import cz.payola.common.rdf.ontology._
import com.hp.hpl.jena

/**
  * An ontology factory.
  */
object Ontology
{
    /**
      * Returns an empty ontology.
      */
    def empty: Ontology = new Ontology(Map.empty[String, Class], Map.empty[String, List[String]])

    /**
      * Creates a new ontology from a string which contains a XML document with either RDFS or OWL ontology.
      * @param ontology XML document.
      * @return A new instance of the ontology class.
      */
    def apply(ontology: String): Ontology = {
        val reader = new java.io.StringReader(ontology)

        // At this moment we don't allow any other format/ as both OWL and RDFS should theoretically come in XML
        // format only.
        val inputType = "RDF/XML"

        // Create a model and read it from the input string.
        val jenaModel = jena.rdf.model.ModelFactory.createOntologyModel()
        jenaModel.read(reader, inputType)

        val result = Ontology(jenaModel)
        jenaModel.close()
        result
    }

    /**
      * Creates a new ontology from the specified [[com.hp.hpl.jena.ontology.OntModel]].
      * @param ontologyModel Jena's ontology representation.
      * @return A new instance of the ontology class.
      */
    def apply(ontologyModel: jena.ontology.OntModel): Ontology = {
        // Process all properties in the ontology.
        val properties = processProperties(ontologyModel)

        // Process all classes in the ontology
        val classes = mutable.HashMap.empty[String, Class]
        val superClasses = mutable.HashMap.empty[String, mutable.Buffer[String]]

        val classIterator = ontologyModel.listClasses()
        while (classIterator.hasNext) {
            val ontClass = classIterator.next
            val uri = ontClass.getURI
            if (uri != null) {
                // Process the class.
                val classProperties = properties.getOrElse(uri, Nil).toMap
                classes.put(uri, new Class(uri, ontClass.getLabel(null), ontClass.getComment(null), classProperties))

                // Process the super classes of the class.
                val superClassIterator = ontClass.listSuperClasses()
                val classSuperClasses = superClasses.getOrElseUpdate(uri, mutable.Buffer.empty[String])
                while (superClassIterator.hasNext) {
                    classSuperClasses += superClassIterator.next.getURI
                }
            }
        }

        new Ontology(classes.toMap, superClasses.mapValues(_.toList).toMap)
    }

    /**
      * Processes all properties of the specified ontology model.
      * @param ontologyModel The ontology model to process.
      * @return A map with property collections indexed by the property domain class URIs. A value in the resulting map
      *         is a map of properties indexed by their URIs.
      */
    private def processProperties(ontologyModel: jena.ontology.OntModel): Map[String, Map[String, Property]] = {
        val properties = mutable.HashMap.empty[String, mutable.HashMap[String, Property]]
        val propertyIterator = ontologyModel.listAllOntProperties()
        while (propertyIterator.hasNext) {
            val property = propertyIterator.next
            processProperty(property).foreach { p =>
                val domainUri = property.getDomain.getURI
                val classProperties = properties.getOrElseUpdate(domainUri, mutable.HashMap.empty[String, Property])
                classProperties.put(p.uri, p)
            }
        }
        properties
    }

    /**
      * Converts the Jena property representation to a [[cz.payola.common.rdf.ontology.Property]] if it conforms to it.
      * Otherwise returns [[scala.None]].
      * @param property The Jena property to convert.
      * @return The converted property.
      */
    private def processProperty(property: jena.ontology.OntProperty): Option[Property] = {
        val domain = property.getDomain
        if (property.getURI != null && domain != null) {
            Some(new Property(property.getURI, Option(property.getRange).map(_.getURI)))
        } else {
            None
        }
    }
}
