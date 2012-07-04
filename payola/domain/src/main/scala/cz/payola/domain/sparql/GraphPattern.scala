package cz.payola.domain.sparql

import collection.immutable.Seq

object GraphPattern
{
    def apply(triple: TriplePattern): GraphPattern = {
        GraphPattern(List(triple))
    }

    def empty: GraphPattern = {
        GraphPattern(Nil)
    }
}

case class GraphPattern(triples: collection.Seq[TriplePattern], optionals: collection.Seq[GraphPattern] = Nil, filters: collection.Seq[Filter] = Nil)
{
    def isEmpty: Boolean = {
        triples.isEmpty && optionals.forall(_.isEmpty) && filters.isEmpty
    }

    def +(pattern: GraphPattern): GraphPattern = {
        GraphPattern(triples ++ pattern.triples, optionals ++ pattern.optionals, filters ++ pattern.filters)
    }

    def +(pattern: Option[GraphPattern]): GraphPattern = {
        pattern match {
            case Some(p) => this + p
            case None => this
        }
    }

    override def toString: String = {
        (triples.map(_.toString) ++ optionals.map(optionalToString(_)) ++ filters.map(_.toString)).mkString("\n")
    }

    private def optionalToString(graphPattern: GraphPattern): String = {
        if (!graphPattern.isEmpty) "OPTIONAL { " + graphPattern + " }" else ""
    }
}
