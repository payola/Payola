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
        new cz.payola.common.rdf.LiteralVertex(value, language)
    }
}
