package cz.payola.domain.sparql

object Uri {

    def getTypePropertyURI = new Uri("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
}

case class Uri(value: String) extends Subject with Predicate with Object
{
    override def toString: String = {
        "<" + value + ">"
    }
}
