package cz.payola.domain.sparql

case class Filter(expression: String)
{
    override def toString: String = {
        "FILTER (%s)".format(expression)
    }
}
