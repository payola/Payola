package cz.payola.common.rdf.ontology


trait Property
{
    val URI: String

    val typeURI: Option[String]
}
