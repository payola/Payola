package cz.payola.domain.rdf.ontology

import scala.collection.mutable._

/** Private class within the ontology package that merges two ontologies into a single
  * one.
  *
  */

private[ontology] object OntologyMerger {

    /** Merges two ontologies into a new one. Equivalent to o1 + o2.
      *
      * @param o1 First ontology.
      * @param o2 Second ontology.
      * @return New instance with merged classes and properties.
      */
    def apply(o1: Ontology, o2: Ontology): Ontology = {
        val merger = new OntologyMerger(o1, o2)
        merger.merge
    }
}

private[ontology] class OntologyMerger(o1: Ontology, o2: Ontology)
{

    // URI -> Class hash map
    private val classes = new HashMap[String, Class]()
    private var mergedOntology: Ontology = null


    private def addClass(cl: Class) {
        if (classes.get(cl.URI).isEmpty){
            val realClass = new Class(cl.URI, cl.label, cl.comment)
            classes.put(realClass.URI, realClass)
        }
    }

    private def createMergedOntology() {
        // First of all, add all the classes to the hash map, because when merging
        // classes, we need to be able to merge the superclasses as well, however,
        // we don't want to keep references to the original classes in the original
        // ontology objects
        o1.classes foreach { cl: Class =>
            addClass(cl)
        }
        o2.classes foreach { cl: Class =>
            addClass(cl)
        }

        // Now process classes (this will merge properties and superclasses
        // of classes that appear in both ontologies and copy them over if not).
        o1.classes foreach { cl: Class =>
            processClass(cl)
        }
        o2.classes foreach { cl: Class =>
            processClass(cl)
        }

        mergedOntology = new Ontology(classes.values.toList)
    }

    def merge: Ontology = {
        if (mergedOntology == null) {
            createMergedOntology()
        }
        mergedOntology
    }

    private def mergeOntologyClasses(cl1: Class, cl2: Class) {
        // Assumptions:
        // - cl1 is in o1
        // - cl2 is in o2
        // - classes with cl1.URI and cl2.URI are already allocated and placed in the
        //      classes hash map


        assert(cl1.URI == cl2.URI, "URI's must match when merging classes!")
        val cl = classes(cl1.URI)

        // Now we need to merge properties
        val props = new ListBuffer[Property]()

        // Add all props from the first class
        props ++= cl1.properties

        // Add missing properties from the second
        cl2.properties foreach { p: Property =>
            if (props.find { prop: Property => prop.URI == p.URI}.isEmpty) {
                // Not found
                props += p
            }
        }
        cl.properties = props

        // Now superclasses.
        val superclasses = ListBuffer[Class]()
        cl1.superclasses foreach { superclass: Class =>
            superclasses += classes(superclass.URI)
        }

        // Add superclasses from cl2, just watch for duplicates
        cl2.superclasses foreach { superclass: Class =>
            if (superclasses.find { c: Class => c.URI == superclass.URI}.isEmpty) {
                superclasses += classes(superclass.URI)
            }
        }

        cl.superclasses = superclasses
    }

    private def processClass(cl: Class) {
        // Assumption for cl:
        // - is a member of o1
        // - a class with the same URI is already allocated in the classes hash map

        val classInOtherOntology: Option[Class] = o2.classes.find { c: Class =>
            c.URI == cl.URI
        }

        if (classInOtherOntology.isDefined){
            // Need to merge them
            mergeOntologyClasses(cl, classInOtherOntology.get)
        }else{
            // Just copy over properties and superclass
            val realClass = classes(cl.URI)
            realClass.properties = cl.properties.toList

            val superclasses = ListBuffer[Class]()
            cl.superclasses foreach { superclass: Class =>
                superclasses += classes(superclass.URI)
            }
            realClass.superclasses = superclasses
        }

    }

}
