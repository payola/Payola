package s2js.adapters.js.dom

abstract class Document
{
    val body: Element = null

    def execCommand(command: String, showDefaultUI: Boolean, value: String) {}

    def getElementById(id: String): Element = null

    def getElementsByClassName(cssClass: String): Seq[Element] = null

    // Definition of the function is there only for compilation purposes, bacause it can't be null.
    def createElement[A <: Element](name: String): A = body.asInstanceOf[A]

    val documentElement: Element = null

    def createTextNode(s: String): Element = null
}
