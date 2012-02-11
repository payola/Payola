package s2js.adapters.js.dom

abstract class Element extends Node {
    val id: String = ""

    var innerHTML = ""

    var className = ""

    def appendChild(e: Element) {}
}