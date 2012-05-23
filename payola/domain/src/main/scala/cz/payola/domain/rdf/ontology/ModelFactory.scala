package cz.payola.domain.rdf.ontology

import scala.collection.mutable.ListBuffer
import com.hp.hpl.jena.ontology._

private[ontology] class ModelFactory(val ontologyModel: OntModel) {

    private val classes: collection.mutable.Seq[Class] = new ListBuffer[Class]()
    private var model: Model = null

    /** Actually creates a new Model instance from OntologyModel.
      */
    private def createModel {
        val clIt = ontologyModel.listClasses()
        while (clIt.hasNext) {
            val cl: OntClass = clIt.next()
            val clURI = cl.getURI

            println("Class " + clURI)

        }

        model = new Model(classes)
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



}
