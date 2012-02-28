package s2js.adapters.js.dom

abstract class Canvas extends Element
{
    var height: Double = 0

    var width: Double = 0

    def getContext[A <: CanvasContext](contextId: String): A
}
