package cz.payola.domain.rdf

import com.hp.hpl.jena.rdf.model._

private[rdf] object LiteralVertex
{
    /**
      * Creates a new literal vertex based on the specified Jena Literal and Jena Statement.
      * @param literal The Jena representation of the literal.
      * @param statement The Jena representation of the statement where the literal is used.
      * @return A new literal vertex.
      */
    def apply(literal: Literal, statement: Statement): cz.payola.common.rdf.LiteralVertex = {
        val language = Option(if (statement.getLanguage != "") statement.getLanguage else null)
        val value = try {
            literal.getValue
        } catch {
            case _ => literal.getString
        }

        // Making sure value is of primitive types, otherwise, convert to string
        val primitiveValue = value match {
            case b: java.lang.Boolean => if (b) "true" else "false"
            case i: java.lang.Integer => i
            case d: java.lang.Double => d
            case s: String => s
            case otherObj => otherObj.toString()
        }

        new cz.payola.common.rdf.LiteralVertex(primitiveValue, language)
    }
}
