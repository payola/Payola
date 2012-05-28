package cz.payola.domain.rdf.ontology

import com.hp.hpl.jena.ontology._
import scala.collection.mutable._
import com.hp.hpl.jena.util.iterator.ExtendedIterator

/** A private (within the ontology package) class that creates the Ontology object
  * from Jena's ontology representation.
  *
  * @param ontologyModel Jena's ontology representation.
  */
private[ontology] class OntologyFactory(val ontologyModel: OntModel) {

    // A hash map of ontology classes for quick access URI -> Class
    private val classes: HashMap[String, Class] = new HashMap[String, Class]()

    // A hash map of ontology properties for quick access URI -> Property
    private val properties: HashMap[String, Property] = new HashMap[String, Property]()

    // Created ontology object
    private var ontology: Ontology = null

    /** Actually creates a new Model instance from OntologyModel.
      */
    private def createOntology {
        val clIt = ontologyModel.listClasses()
        while (clIt.hasNext) {
            val cl: OntClass = clIt.next()
            processClass(cl)
        }

        ontology = new Ontology(classes.values.toList)
    }

    /** Gets a class for URI or creates a new class from @ontClass.
      *
      * @param uri URI of the class.
      * @param ontClass Jena's ontology class from which the class is created in case
      *                 it hasn't been added to the classes hash map.
      * @return Ontology class corresponding to the URI.
      */
    private def getClassForURI(uri: String, ontClass: OntClass): Class = {
        classes.getOrElse(uri, {
            val cl = new Class(uri, ontClass.getLabel(null), ontClass.getComment(null))
            classes.put(uri, cl)
            cl
        })
    }


    /** Returns an instance of Ontology. Can be called multiple times, however,
      * same instance will be returned each time.
      *
      * @return Ontology instance generated from the OntologyModel.
      */
    def getOntology: Ontology = {
        if (ontology == null) {
            createOntology
        }

        ontology
    }

    /** Processes a class - creates it if necessary; adds superclasses and properties.
      *
      * @param ontClass Jena's ontology class.
      * @return Ontology class equivalent to the @ontClass.
      */
    private def processClass(ontClass: OntClass): Class = {
        val clURI = ontClass.getURI

        val internalCl = getClassForURI(clURI, ontClass)
        val superclasses = new ListBuffer[Class]()
        val props= new ListBuffer[Property]()

        val superclassIterator: ExtendedIterator[OntClass] = ontClass.listSuperClasses()
        while (superclassIterator.hasNext){
            val sclass = superclassIterator.next()
            superclasses += processClass(sclass)
        }

        val propertyIterator: ExtendedIterator[OntProperty] = ontClass.listDeclaredProperties()
        while (propertyIterator.hasNext){
            val prop = propertyIterator.next()
            props += processProperty(prop)
        }

        internalCl.superclasses = superclasses
        internalCl.properties = props
        internalCl
    }

    /** Processes a property - creates it if necessary.
      *
      * @param ontProperty Jena's ontology property.
      * @return Ontology property equivalent to @ontProperty.
      */
    private def processProperty(ontProperty: OntProperty): Property = {
        val propURI = ontProperty.getURI

        val propType = ontProperty.getRDFType
        val propClass = if (propType == null) {
            None
        }else{
            Option(propType.getURI)
        }

        properties.getOrElse(propURI, {
            val p = new Property(propURI, propClass)
            properties.put(propURI, p)
            p
        })
    }


}
