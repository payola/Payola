package s2js.adapters.dom

trait DocumentType extends Node
{
    val name: String

    val entities: NamedNodeMap[Entity]

    val notations: NamedNodeMap[Notation]

    val publicId: String

    val systemId: String

    val internalSubset: String
}
