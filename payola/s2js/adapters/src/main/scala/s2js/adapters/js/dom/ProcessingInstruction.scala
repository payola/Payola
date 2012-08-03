package s2js.adapters.js.dom

trait ProcessingInstruction extends Node
{
    val target: String

    var data: String
}
