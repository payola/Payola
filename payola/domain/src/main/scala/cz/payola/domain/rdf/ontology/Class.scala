package cz.payola.domain.rdf.ontology


class Class(val URI: String, val superclasses: collection.Seq[Class], val properties: Seq[Property])
{
}
