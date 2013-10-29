package cz.payola.domain.sparql

/**
 * An object representation of a SPARQL LIMIT statement
 * @author Jiri Helmich
 * @param limit Limiting size of the resultset.
 */
case class Limit(limit: Int)
{
    override def toString: String = {
        "LIMIT %d".format(limit)
    }
}
