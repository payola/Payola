package s2js.adapters.js.dom


abstract class Document
{
    val body: Element = null

    def execCommand(command: String, showDefaultUI: Boolean, value: String) {}
}