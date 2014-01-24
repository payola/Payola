package cz.payola.domain.sparql

import scala.collection.immutable

/**
 * Modified by Jiri Helmich in order to provide limit functionality.
 */

object ConstructQuery
{
    def apply(graphPattern: GraphPattern, limit: Limit): ConstructQuery = {
        ConstructQuery(graphPattern.triplePatterns, Some(graphPattern), Some(limit))
    }

    def apply(graphPattern: GraphPattern, limit: Option[Limit]): ConstructQuery = {
        ConstructQuery(graphPattern.triplePatterns, Some(graphPattern), limit)
    }

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

case class ConstructQuery(template: immutable.Seq[TriplePattern], pattern: Option[GraphPattern], limit: Option[Limit] = None)
{
    def isEmpty: Boolean = {
        template.isEmpty && pattern.isEmpty
    }

    override def toString: String = {
        """
        CONSTRUCT {
            %s
        }
        %s
        %s
        """.format(
            template.mkString("\n"),
            pattern.map(p => "WHERE { %s }".format(p)).getOrElse(""),
            limit.map(l => l).getOrElse("")
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

        // handle limit, take maximum of both (if defined)
        val l = if (limit.isDefined){
            if (constructQuery.limit.isDefined){
                Some(Limit(scala.math.max(limit.get.limit,constructQuery.limit.get.limit)))
            }else{
                limit
            }
        }else{
            constructQuery.limit
        }
        ConstructQuery(margedTemplate, mergedPattern, l)
    }
}
