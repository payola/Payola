package cz.payola.common.rdf.ontology


trait Class
{
    val URI: String
    val label: String
    val comment: String

    def properties: collection.Seq[Property]
    def superclasses: collection.Seq[Class]
}
