package s2js.adapters.js.dom

abstract class Entity extends Node
{
    val notationName: String

    val publicId: String

    val systemId: String
}
