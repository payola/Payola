package cz.payola.domain.rdf.ontology

import com.hp.hpl.jena.ontology._
import scala.collection.mutable._
import com.hp.hpl.jena.util.iterator.ExtendedIterator

private[ontology] class OntologyFactory(val ontologyModel: OntModel) {

    // Need a hash map
    private val classes: HashMap[String, Class] = new HashMap[String, Class]()
    private val properties: HashMap[String, Property] = new HashMap[String, Property]()
    private var model: Model = null

    /** Actually creates a new Model instance from OntologyModel.
      */
    private def createModel {
        val clIt = ontologyModel.listClasses()
        while (clIt.hasNext) {
            val cl: OntClass = clIt.next()
            processClass(cl)
        }

        model = new Model(classes.values.toList)
    }


    private def getClassForURI(uri: String, ontClass: OntClass): Class = {
        classes.getOrElse(uri, {
            val cl = new Class(uri, ontClass.getLabel(null), ontClass.getComment(null))
            classes.put(uri, cl)
            cl
        })
    }


    /** Returns an instance of Model. Can be called multiple times, however,
      * same instance will be returned each time.
      *
      * @return Model instance generated from the OntologyModel.
      */
    def getModel: Model = {
        if (model == null) {
            createModel
        }

        model
    }


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

    private def processProperty(ontProperty: OntProperty): Property = {
        val propURI = ontProperty.getURI
        properties.getOrElse(propURI, {
            val p = new Property(propURI)
            properties.put(propURI, p)
            p
        })
    }


}
