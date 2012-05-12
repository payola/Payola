package cz.payola.domain.sparql

case class TriplePattern(subject: Subject, predicate: Predicate, obj: Object)
{
    override def toString: String = {
        "%s %s %s .".format(subject, predicate, obj)
    }
}
