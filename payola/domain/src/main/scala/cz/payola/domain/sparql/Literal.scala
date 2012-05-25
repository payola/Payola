package cz.payola.domain.sparql

case class Literal(value: String) extends Object
{
    override def toString: String = {
        value
    }
}
