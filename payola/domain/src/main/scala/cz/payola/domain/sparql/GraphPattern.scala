package cz.payola.domain.sparql

import scala.collection.immutable
import cz.payola.common.rdf.Edge

object GraphPattern
{
    def apply(triple: TriplePattern): GraphPattern = {
        GraphPattern(List(triple))
    }

    def empty: GraphPattern = {
        GraphPattern(Nil)
    }

    def optionalProperties(subject: Subject): immutable.Seq[GraphPattern] = {
        optionalProperties(subject, new VariableGenerator)
    }

    def optionalProperties(subject: Subject, variableGetter: () => Variable): immutable.Seq[GraphPattern] = {
        (Edge.rdfTypeEdge :: Edge.rdfLabelEdges).map { e =>
            GraphPattern(TriplePattern(subject, Uri(e), variableGetter()))
        }
    }
}

case class GraphPattern(
    triples: immutable.Seq[TriplePattern],
    optionals: immutable.Seq[GraphPattern] = Nil,
    filters: immutable.Seq[Filter] = Nil)
{
    def isEmpty: Boolean = {
        triples.isEmpty && optionals.forall(_.isEmpty) && filters.isEmpty
    }

    def triplePatterns: immutable.Seq[TriplePattern] = {
        triples ++ optionals.flatMap(_.triplePatterns)
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