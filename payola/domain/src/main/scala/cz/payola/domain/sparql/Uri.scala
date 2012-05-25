package cz.payola.domain.sparql

case class Uri(value: String) extends Subject with Predicate with Object
{
    override def toString: String = {
        "<" + value + ">"
    }
}
