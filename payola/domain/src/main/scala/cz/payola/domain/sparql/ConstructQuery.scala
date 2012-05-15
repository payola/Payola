package cz.payola.domain.sparql

import collection.immutable

object ConstructQuery
{
    def apply(triples: immutable.Seq[TriplePattern]): ConstructQuery = {
        ConstructQuery(triples, Some(GraphPattern(triples)))
    }

    def apply(triple: TriplePattern): ConstructQuery = {
        ConstructQuery(List(triple))
    }
}

case class ConstructQuery(template: immutable.Seq[TriplePattern], pattern: Option[GraphPattern])
{
    override def toString: String = {
        """
        CONSTRUCT {
            %s
        }
        %s
        """.format(
            template.mkString("\n"),
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
