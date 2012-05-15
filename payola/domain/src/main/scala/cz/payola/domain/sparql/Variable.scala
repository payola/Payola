package cz.payola.domain.sparql

case class Variable(name: String) extends Subject with Predicate with Object
{
    override def toString: String = {
        "?" + name
    }
}
