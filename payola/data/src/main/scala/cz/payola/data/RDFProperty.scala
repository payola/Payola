package cz.payola.data

import com.hp.hpl.jena.rdf.model.{Resource, Property, Statement}

object RDFProperty {
    def apply(statement: Statement): RDFProperty = {
        //val subject: Resource = statement.getSubject
        val predicate: Property = statement.getPredicate
        val rdfNode: com.hp.hpl.jena.rdf.model.RDFNode = statement.getObject

        new RDFProperty(predicate.getLocalName, predicate.getNameSpace, rdfNode.asLiteral.toString)
    }
}

class RDFProperty(val name: String, val namespace: String, val value: String)
