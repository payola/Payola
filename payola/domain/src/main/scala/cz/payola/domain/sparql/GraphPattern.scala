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

case class GraphPattern(triples: Seq[TriplePattern], optionals: Seq[GraphPattern] = Nil, filters: Seq[Filter] = Nil)
{
    override def toString: String = {
        (triples.map(_.toString) ++ optionals.map("OPTIONAL { " + _ + " }") ++ filters.map(_.toString)).mkString("\n")
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
}
