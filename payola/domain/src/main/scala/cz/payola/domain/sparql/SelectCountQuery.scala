package cz.payola.domain.sparql

import collection.immutable

object SelectCountQuery {
    def apply(graphPattern: GraphPattern): ConstructQuery = {
        ConstructQuery(graphPattern.triplePatterns, Some(graphPattern))
    }

    def apply(triples: immutable.Seq[TriplePattern]): ConstructQuery = {
        ConstructQuery(triples, Some(GraphPattern(triples)))
    }

    def apply(triple: TriplePattern): ConstructQuery = {
        ConstructQuery(List(triple))
    }

    def empty: ConstructQuery = {
        ConstructQuery(Nil)
    }
}

class SelectCountQuery (template: immutable.Seq[TriplePattern], pattern: Option[GraphPattern])
{
    def isEmpty: Boolean = {
        template.isEmpty && pattern.isEmpty
    }

    override def toString: String = {
        """
        SELECT count(*)
        %s
        """.format(
            pattern.map(p => "WHERE { %s }".format(p)).getOrElse("")
        )
    }

    def +(constructQuery: ConstructQuery): ConstructQuery = {
        val margedTemplate = template ++ constructQuery.template
        val patterns = (pattern.toList ++ constructQuery.pattern.toList)
        val mergedPattern = if (patterns.nonEmpty) {
            Some(patterns.reduce(_ + _))
        } else {
            None
        }
        ConstructQuery(margedTemplate, mergedPattern)
    }

}
