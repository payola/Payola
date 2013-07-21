package cz.payola.domain.sparql

case class Limit(limit: Int)
{
    override def toString: String = {
        "LIMIT %d".format(limit)
    }
}
