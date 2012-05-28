package cz.payola.domain.rdf.ontology


class Property(val URI: String) extends cz.payola.common.entities.ontology.Property
{
    override def toString = super.toString + " => " + URI
}
